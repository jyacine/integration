package com.oculus.task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TestEx {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newFixedThreadPool(4);
		Callable<Integer> task = () -> {
		        System.out.println("task");
		        return 123;

		};
		Future<Integer> future = null;
		try{
			future = executor.submit(() -> {
			    process();
				return 123;
			});} catch(Exception e){
				e.printStackTrace();
				executor.shutdown();
			}
		executor.submit(task);
		executor.submit(task);
		executor.submit(task);
		
		
		//System.out.println(future.get());
		//executor.shutdown();
	}
	
	
	private static void process() throws IntegrationException {
		throw new IntegrationException("error");
	}

}
