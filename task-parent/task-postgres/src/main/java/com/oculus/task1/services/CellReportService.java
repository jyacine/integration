package com.oculus.task1.services;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.hibernate.secure.spi.IntegrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.oculus.task.ReportService;
import com.oculus.task1.model.IntegrationLineError;
import com.oculus.task1.model.IntegrationReport;
import com.oculus.task1.model.enums.ErrorLine;
import com.oculus.task1.repository.ReportRepository;

@Service
@Transactional
public class CellReportService implements ReportService<UUID> {

	@Autowired
	private ReportRepository reportRepository;
	
	public static final String STATUS_FAILED = "Failed";
	public static final String STATUS_SUCCESS = "Success";
	public static final String STATUS_STARTED = "Started";
	
	private static final Logger log = LoggerFactory.getLogger(CellReportService.class);

	
	@Override
	public UUID createNewReport(File file) {
		IntegrationReport report = reportRepository.findReportByFilename(file.getName());
		if(report == null){
			report = new IntegrationReport();
			report.setFilename(file.getName());
		}else if(report.getStatus().equals(STATUS_STARTED)) {
			throw new IntegrationException("file with the same name" + file.getName() + "currently running");
		}
		report.setLinesError(null);
		report.setLinesFiltered(0);
		report.setLinesProcessed(0);
		report.setStarted(new Date());
		report.setStatus(STATUS_STARTED);
		return reportRepository.save(report).getId();
	}

	@Override
	public void endIntegration(UUID reportId, boolean b) {
		Optional<IntegrationReport> report = reportRepository.findById(reportId);
		report.get().setFinish(new Date());
		report.get().setStatus(b?STATUS_SUCCESS:STATUS_FAILED);
		
	}
	
	@Override
	public void addNbEntitiesProcessed(UUID reportId, int linesProcessed) {
		Optional<IntegrationReport> report = reportRepository.findById(reportId);
		int currentLines = report.get().getLinesProcessed();
		log.info("add <{}> number of lines processed. report file <{}>",linesProcessed+currentLines,report.get().getFilename());
		report.get().setLinesProcessed(linesProcessed+currentLines);
	}

	@Override
	public void addNbEntitiesStored(UUID reportId, int linesProcessed) {
		Optional<IntegrationReport> report = reportRepository.findById(reportId);
		int currentLines = report.get().getLinesStored();
		log.info("add <{}> number of lines stored. report file <{}>",linesProcessed+currentLines,report.get().getFilename());
		report.get().setLinesStored(linesProcessed+currentLines);
	}
	
	
	/**
	 * Add report when line cell insertion failed
	 * @param reportId idReport
	 * @param line line from csv
	 */
	public void addLineStoreFailed(UUID reportId, String line){
		addLineError(reportId, line, ErrorLine.STOREFAILED);
	}
	
	public void addLineStoreFailed(UUID reportId, List<String> lines) {
		for (String line : lines) {
			addLineStoreFailed(reportId, line);
		}
	}
	
	public void addLineWrongFormat(UUID reportId, String line){
		addLineError(reportId, line, ErrorLine.FORMATFAILED);
	}
	
	public void addLineFiltered(UUID reportId, int linesFiltered){
		Optional<IntegrationReport> report = reportRepository.findById(reportId);
		int currentlines = report.get().getLinesFiltered();
		log.debug("add <{}> number of lines filtered. report file <{}>",currentlines+linesFiltered,report.get().getFilename());
		report.get().setLinesFiltered(linesFiltered+currentlines);
	}

	
	@Transactional(readOnly=false,isolation=Isolation.READ_COMMITTED)
	public void addLineError(UUID reportId, String line, ErrorLine error){
		Optional<IntegrationReport> report = reportRepository.findById(reportId);
		IntegrationLineError errorLine = new IntegrationLineError(null,report.get().getId(), line, error);
		Set<IntegrationLineError> list = report.get().getLinesError();
		list.add(errorLine);
	}

	public IntegrationReport getLastReport() {
		return reportRepository.findTopByOrderByIdDesc();
		
	}

	public IntegrationReport getRecordById(UUID reportId) {
		Optional<IntegrationReport> integ = reportRepository.findById(reportId);
		return (!integ.isPresent())?null:integ.get();
	}
	
	public List<IntegrationReport> getByStartedTimeBetween(Date startBegin,
			Date startEnd){
		return reportRepository.findByStartedBetween(startBegin, startEnd);
	}

	public IntegrationReport findReportByFilename(String fileName) {
		return reportRepository.findReportByFilename(fileName);
	}

	public int getNbLinesError(UUID id) {
		return reportRepository.findById(id).get().getLinesError().size();
	}
}
