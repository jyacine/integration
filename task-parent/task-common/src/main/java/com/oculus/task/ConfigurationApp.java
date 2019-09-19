package com.oculus.task;

import java.io.File;

import javax.annotation.PostConstruct;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationApp {
	
	@Value("${oculus.task.directory}")
    private String integDirectory;
	
	public static String INPUT_DIR;
	public static String INPROGRESS_DIR;
	public static String OUTPUT_DIR;
	public static String ERROR_DIR;

	@PostConstruct
	public 	void init(){
		INPUT_DIR = integDirectory + File.separator + "input";
		INPROGRESS_DIR = integDirectory + File.separator + "inprogress";
		OUTPUT_DIR = integDirectory + File.separator + "output";
		ERROR_DIR = integDirectory + File.separator + "error";
	}
	
	@Bean
    public FileAlterationObserver getFileObserver(){
    	return new FileAlterationObserver(INPUT_DIR);
    }
    
    @Bean
    public FileAlterationMonitor  getFileMonitor(){
    	return new FileAlterationMonitor(2000);
    }

}
