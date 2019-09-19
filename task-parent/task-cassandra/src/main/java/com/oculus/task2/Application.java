package com.oculus.task2;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.oculus.task.ConfigurationApp;
import com.oculus.task.IntegrationFileListener;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("com.oculus")
public class Application {
		
	public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        FileAlterationObserver observer = context.getBean(FileAlterationObserver.class);
        FileAlterationMonitor monitor = context.getBean(FileAlterationMonitor.class);
        IntegrationFileListener<?, ?> listener = context.getBean(IntegrationFileListener.class);
        observer.addListener(listener);
        monitor.addObserver(observer);
        monitor.start();
        
        //Just for task
        //Creating the folders
        //The folder must be created before running the app
        //But we do it just for this exercice
        createFolders(); 
    }
	
	private static void createFolders(){
		new File(ConfigurationApp.ERROR_DIR).mkdirs();
		new File(ConfigurationApp.INPROGRESS_DIR).mkdirs();
		new File(ConfigurationApp.INPUT_DIR).mkdirs();
		new File(ConfigurationApp.OUTPUT_DIR).mkdirs();
	}
}