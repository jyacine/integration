package com.oculus.task;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
public class IntegrationStepsProcess<I,E> {
	
	public static final int THREASHOLD_FLUSH = 30000;
	public static final int THREAD_POOL = 10;
	
	@Autowired
	ReportService<I> reportService;
	
	@Autowired
	IntegrationService<I,E> integService;
	
	ExecutorService executor;
	
	
	
	private static final Logger log = LoggerFactory.getLogger(IntegrationStepsProcess.class);
	
	@PostConstruct
	private void init(){
		executor = Executors.newFixedThreadPool(THREAD_POOL);
	}
	
	@PreDestroy
	private void destroy(){
		log.info("Shutdown executor...");
		executor.shutdown();
		log.info("Shutdown executor completed");
	}
	
	public void start(File file, I reportId) throws IntegrationException, InterruptedException, ExecutionException{
		log.info("starting processing file <{}>", file.getAbsoluteFile());
		integService.clearLastReport(file);
		List<E> listBufferEntities = new ArrayList<E>();
		if(!checkFile(file)){
			throw new IntegrationException("File format error");
		}

		Future<Integer> reader = executor.submit(() -> {
			try{
				return integService.processRead(file,reportId,listBufferEntities);
			}catch(IntegrationException e){
				e.printStackTrace();
				return -1;
			}
		});
		
		List<Future<Integer>> entitiesSaved = new ArrayList<>();
		Integer nbEntities;
		while(!reader.isDone()){
			Iterator<Future<Integer>> iterWrite = entitiesSaved.iterator();
			while (iterWrite.hasNext()) {
				   Future<Integer> future = iterWrite.next();
				   if(future.isDone()){
					   nbEntities = future.get();
					   if(nbEntities == -1){
						   throw new IntegrationException("error while saving entities");
					   }
					   reportService.addNbEntitiesStored(reportId, nbEntities);
					   iterWrite.remove();
				   }
			}
			
			if(listBufferEntities.size() >= THREASHOLD_FLUSH){
				synchronized (listBufferEntities) {
					List<E> cpEntities = new ArrayList<E>(listBufferEntities.size());
					cpEntities.addAll(listBufferEntities);
					entitiesSaved.add(executor.submit(new ProcessWriter(reportId, cpEntities)));
					listBufferEntities.clear();
				}
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				
			}
		}
		Integer write = new ProcessWriter(reportId, listBufferEntities).call();
		boolean integrationStatus = (reader.get() != -1 && write != -1);
		write = (write==-1)?0:write;
		if(write != -1){
			for (Future<Integer> future : entitiesSaved) {
				if(future.get() != -1){
					write =+ future.get();
				}
			}
			reportService.addNbEntitiesStored(reportId, write);
		}
		reportService.addNbEntitiesProcessed(reportId, reader.get());
		log.info("end processing file <{}> , Success <{}>", file.getAbsoluteFile(),integrationStatus);
		if(!integrationStatus){
			throw new IntegrationException("Error while integration " + file.getAbsolutePath());
		}

	}
	
	boolean checkFile(File file){
		try {
			integService.checkFile(file);
		} catch (IntegrationException e1) {
			e1.printStackTrace();
			return false;
		}
		return true;
	}

	@AllArgsConstructor
	public class ProcessWriter implements Callable<Integer> {
				
		I reportId;
		
		List<E> entities;

		@Override
		public Integer call() {
			try{
				return integService.processWrite(reportId, entities);
			}catch(IntegrationException e){
				e.printStackTrace();
				return -1;
			}
		}
	}
}
