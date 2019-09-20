package com.oculus.task.test;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.oculus.task.ConfigurationApp;
import com.oculus.task.IntegrationFileListener;
import com.oculus.task2.Application;
import com.oculus.task2.model.PacketReport;
import com.oculus.task2.service.PacketIntegrationService;
import com.oculus.task2.service.PacketReportService;

@SpringBootTest(classes=Application.class)
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ComponentScan(basePackages={"com.oculus.task2"})
public class TechnicalTaskTestIT {

	private static final Logger log = LoggerFactory.getLogger(TechnicalTaskTestIT.class);
 
	@Autowired
	PacketIntegrationService integrationService;
	
	@Autowired
	PacketReportService reportService;
	
	@Value("${oculus.task.directory}")
    private String integDirectory;
	
	@Value("${oculus.task2.dircontent}")
    private String contentDirectory;
	
	@Value("${oculus.test.testFile}")
    private String testFilePath;
	
	@Autowired
	private ApplicationContext appContext;
	
	@Before
	public void init() throws Exception{
		createFolders();
        FileAlterationObserver observer = appContext.getBean(FileAlterationObserver.class);
        FileAlterationMonitor monitor = appContext.getBean(FileAlterationMonitor.class);
        IntegrationFileListener listener = appContext.getBean(IntegrationFileListener.class);
        observer.addListener(listener);
        monitor.addObserver(observer);
        monitor.start();
	}
	
	@After
	public void end() throws Exception {
		FileAlterationMonitor monitor = appContext.getBean(FileAlterationMonitor.class);
        monitor.stop();
	}
	
	@Test
	public void technicalTest() throws Exception {
		log.info("***************************************** TECHNICAL TASK BEGIN **************************");
		createFolders();
		String fileName = "test.pcap";
		File fileDest = new File(ConfigurationApp.INPUT_DIR + File.separator + fileName);
		File fileSrc = new File(testFilePath);
		
		log.info("copy <{}> to <{}>",fileSrc.getAbsolutePath(),fileDest.getAbsolutePath());
		FileUtils.copyFile(fileSrc, fileDest);

		PacketReport report = waitUntilIntegrationFinish(fileName);
		log.info("**********************************************************************************"); 
		log.info("********************************* REPORT *****************************************");
		log.info("File integrated 	  <{}> ",report.getKey().getFileName());
		log.info("Date finished   	  <{}> ",report.getKey().getId());
		log.info("Date started    	  <{}> ",report.getStarted());
		log.info("Date finished   	  <{}> ",report.getFinish());
		log.info("packet processed    <{}> ",report.getPacketsProcessed());
		log.info("packet stored   	  <{}> ",report.getPacketsStored());
		log.info("Status          	  <{}> ",report.getStatus());
		log.info("**********************************************************************************"); 
        
		log.info("***************************************** TECHNICAL TASK END **************************");

	}
	
	private void createFolders(){
		new File(ConfigurationApp.ERROR_DIR).mkdirs();
		new File(ConfigurationApp.INPROGRESS_DIR).mkdirs();
		new File(ConfigurationApp.INPROGRESS_DIR).mkdirs();
		new File(ConfigurationApp.INPUT_DIR).mkdirs();
		new File(ConfigurationApp.OUTPUT_DIR).mkdirs();
		new File(contentDirectory).mkdirs();
	}
	
	/**
	 * wait integration finished
	 * @param fileName
	 * @return Report
	 */
	private PacketReport waitUntilIntegrationFinish(String fileName){
		PacketReport report = reportService.findReportByFilename(fileName);
		while(report == null || report.getStatus().equals(PacketReportService.STATUS_STARTED)){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			report = reportService.findReportByFilename(fileName);
		}
		return report;
	}
}
