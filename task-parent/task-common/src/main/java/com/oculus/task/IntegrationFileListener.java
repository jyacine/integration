package com.oculus.task;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

public class IntegrationFileListener<I,E> implements FileAlterationListener {

	private static final Logger log = LoggerFactory.getLogger(IntegrationFileListener.class);
	
	//Limit handling files at the same time
	ExecutorService executor;
	
	private List<Future<ProcessResult<I>>> listProcess = new ArrayList<>();
	
	@Autowired
	IntegrationStepsProcess<I,E> integStepsProcess;
	
	@Autowired
	ReportService<I> reportService;
	
	@Value("${oculus.task.concurrent.files}")
	private int thread_pool;
	
	@PostConstruct
	private void init(){
		executor = Executors.newFixedThreadPool(thread_pool);
	}
	
	@PreDestroy
	private void destroy(){
		log.info("Shutdown executor...");
		executor.shutdown();
		log.info("Shutdown executor completed");
	}
	
	@Override
	public void onDirectoryChange(File arg0) {
		log.error("Directory <{}> changed. NOT ALLOWED",arg0.getAbsolutePath());
	}

	@Override
	public void onDirectoryCreate(File arg0) {
		log.error("Directory <{}> created. NOT ALLOWED",arg0.getAbsolutePath());
	}

	@Override
	public void onDirectoryDelete(File arg0) {
		log.error("Directory <{}> deleted. NOT ALLOWED",arg0.getAbsolutePath());
	}

	@Override
	public void onFileChange(File arg0) {
		log.error("file <{}> changed. NOT ALLOWED",arg0.getAbsolutePath());
	}

	@Override
	public void onFileCreate(File arg0) {
		
	}

	@Override
	public void onFileDelete(File arg0) {

	}

	@Override
	public void onStart(FileAlterationObserver arg0) {
		log.trace("start watching directory <{}>",arg0.getDirectory());
		checkCurrentProcess();
		for (File file : arg0.getDirectory().listFiles()) {
			waitUntilFileCopyFinished(file.getAbsolutePath());
			startIntegrationProcess(file);
		}
	}

	@Override
	public void onStop(FileAlterationObserver arg0) {
	}

	private void waitUntilFileCopyFinished(String fileAbsolutePath){
		Path filePath = Paths.get(fileAbsolutePath);
		FileChannel fileChannel;
		while(true){
			try {
				Thread.sleep(300);
				fileChannel = FileChannel.open(filePath);
				fileChannel.close();
				return;
			} catch (IOException | InterruptedException e) {
	
			}
		}
	}
	
	private void startIntegrationProcess(File file){
		File inprogress = new File(ConfigurationApp.INPROGRESS_DIR 
				+ File.separator + file.getName());
		
		if(!moveFile(file, inprogress)){
			log.warn("Cannot process file <{}>. maybe another file with the same name is already running",file.getName());
			return;
		}
		startProcessingFile(inprogress);
	}
	
	private void startProcessingFile(File file) {
		listProcess.add(executor.submit(() -> {
			I reportId = null;
			try{
				reportId = reportService.createNewReport(file);
				integStepsProcess.start(file,reportId);
				return new ProcessResult<I>(file.getName(), true,reportId);
			}catch(Exception ex){
				ex.printStackTrace();
				return new ProcessResult<I>(file.getName(), false,reportId);
			}
		}));
	}
	
	private void checkCurrentProcess(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh-mm-ss-");  
		String strDate = dateFormat.format(Calendar.getInstance().getTime());
		String PROP_FILENAME = "{FILENAME}";
		String errorFilePath = ConfigurationApp.ERROR_DIR + 
				File.separator + PROP_FILENAME;
		String outputFilePath = ConfigurationApp.OUTPUT_DIR 
				+ File.separator + PROP_FILENAME;
		String inprogressFilePath = ConfigurationApp.INPROGRESS_DIR 
				+ File.separator + PROP_FILENAME;
		
		synchronized (listProcess) {
			Iterator<Future<ProcessResult<I>>> iterProcess = listProcess.iterator();
			while (iterProcess.hasNext()) {
			   Future<ProcessResult<I>> future = iterProcess.next();
			   if(future.isDone()){
				   try {
						ProcessResult<I> result = future.get(5, TimeUnit.SECONDS);
						log.info("end integration file <{}>. Success <{}>",result.fileName,result.isSuccess);
						reportService.endIntegration(result.reportId, result.isSuccess);
						File inprogress = new File(inprogressFilePath.replace(PROP_FILENAME, result.getFileName()));
						if(result.isSuccess){
							File outputFile = new File(outputFilePath.replace(PROP_FILENAME, 
									strDate + result.getFileName()));
							moveFile(inprogress, outputFile);
						}else{
							File errorFile = new File(errorFilePath.replace(PROP_FILENAME, 
									strDate + result.getFileName()));
							moveFile(inprogress, errorFile);
						}
						iterProcess.remove();
				   } catch (InterruptedException | ExecutionException | TimeoutException e) {
					   e.printStackTrace();
				   }
			   }
			}
		}
	}
	
	private boolean moveFile(File fileSrc,File fileDest) {
		try {
			FileUtils.moveFile(fileSrc, fileDest);
		} catch (IOException e) {
			log.error("error while moving file <{}> to  <{}>",fileSrc.getAbsolutePath(),fileDest.getAbsoluteFile());
			return false;
		}
		return true;
	}
	
	@AllArgsConstructor(access=AccessLevel.PRIVATE)
	@Data
	private static class ProcessResult<I> {
		
		private String fileName;
		
		private boolean isSuccess;
		
		private I reportId;
		
	}
}
