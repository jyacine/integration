package com.oculus.task1.test;

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
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.oculus.task.ConfigurationApp;
import com.oculus.task1.Application;
import com.oculus.task1.CellIntegrationFileListener;
import com.oculus.task1.model.IntegrationReport;
import com.oculus.task1.services.CellIntegrationService;
import com.oculus.task1.services.CellReportService;

@SpringBootTest(classes=Application.class)
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ComponentScan(basePackages={"com.oculus.task"})
@Profile("integration")
public class TechnicalTaskTestIT {

	private static final Logger log = LoggerFactory.getLogger(TechnicalTaskTestIT.class);
 
	@Autowired
	CellIntegrationService integrationService;
	
	@Autowired
	CellReportService reportService;
	
	@Value("${oculus.task.directory}")
    private String integDirectory;
	
	@Autowired
	private ApplicationContext appContext;
	
	@Before
	public void init() throws Exception{
		createFolders();
        FileAlterationObserver observer = appContext.getBean(FileAlterationObserver.class);
        FileAlterationMonitor monitor = appContext.getBean(FileAlterationMonitor.class);
        CellIntegrationFileListener listener = appContext.getBean(CellIntegrationFileListener.class);
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
		String fileName = "test.csv";
		File fileDest = new File(ConfigurationApp.INPUT_DIR + File.separator + fileName);
		File fileSrc = new File(getClass().getResource("/"+fileName).getFile());
		
		log.info("move <{}> to <{}>",fileSrc.getAbsolutePath(),fileDest.getAbsolutePath());
		
		FileUtils.moveFile(fileSrc, fileDest);
		
		Thread.sleep(5000);
		IntegrationReport report = waitUntilIntegrationFinish(fileName);
		log.info("**********************************************************************************"); 
		log.info("********************************* REPORT *****************************************");
		log.info("File integrated 	  <{}> ",report.getFilename());
		log.info("Date started    	  <{}> ",report.getStarted().toGMTString());
		log.info("Date finished   	  <{}> ",report.getFinish().toGMTString());
		log.info("Status          	  <{}> ",report.getStatus());
		log.info("Lines processed	  <{}> ",report.getLinesProcessed());
		log.info("Lines inserted 	  <{}> ",report.getLinesStored());
		log.info("Lines insert failed <{}> ",reportService.getNbLinesError(report.getId()));
		log.info("Lines filtered	  <{}> ",report.getLinesFiltered());
		log.info("**********************************************************************************"); 

		//integrationService.processFile("C:\\Users\\XDGT0500\\Downloads\\cell_towers_2019-08-06-T000000.csv");
		//integrationService.processFile("C:\\Users\\XDGT0500\\Downloads\\test.csv");
		
        
		log.info("***************************************** TECHNICAL TASK END **************************");

	}
	
	private void createFolders(){
		new File(ConfigurationApp.ERROR_DIR).mkdirs();
		new File(ConfigurationApp.INPROGRESS_DIR).mkdirs();
		new File(ConfigurationApp.INPROGRESS_DIR).mkdirs();
		new File(ConfigurationApp.INPUT_DIR).mkdirs();
		new File(ConfigurationApp.OUTPUT_DIR).mkdirs();
	}
	
	/**
	 * wait integration finished
	 * @param fileName
	 * @return Report
	 */
	private IntegrationReport waitUntilIntegrationFinish(String fileName){
		IntegrationReport report = reportService.findReportByFilename(fileName);
		while(report.getStatus().equals(CellReportService.STATUS_STARTED)){
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
