package com.oculus.task2.service;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oculus.task.ReportService;
import com.oculus.task2.model.PacketReport;
import com.oculus.task2.model.ReportKey;
import com.oculus.task2.repository.PacketReportRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class PacketReportService implements ReportService<ReportKey> {

	public static final String STATUS_FAILED = "Failed";
	public static final String STATUS_SUCCESS = "Success";
	public static final String STATUS_STARTED = "Started";
	
	@Autowired
	PacketReportRepository packetReportRepo;
	
	@Override
	public ReportKey createNewReport(File file) {
		
		ReportKey reportKey = new ReportKey(file.getName(), UUID.randomUUID());
		PacketReport report = new PacketReport(reportKey, new Date(),
				0,0, 
				new Date(), STATUS_STARTED);
		packetReportRepo.save(report);
		return reportKey;
	}

	@Override
	public void endIntegration(ReportKey reportId, boolean isSucceed) {
		PacketReport report = packetReportRepo.findByKeyIdAndKeyFileName(reportId.getId(),reportId.getFileName());
		//report.get().setFinish(LocalDate.fromMillisSinceEpoch(Instant.ofEpochMilli(new Date().getTime()).atZone(ZoneId.systemDefault()).toEpochSecond()));
		report.setFinish(new Date());
		report.setStatus(isSucceed?STATUS_SUCCESS:STATUS_FAILED);
		packetReportRepo.save(report);
	}

	public PacketReport findReportByFilename(String fileName) {
		return packetReportRepo.findOneByKeyFileName(fileName);
	}

	@Override
	public void addNbEntitiesProcessed(ReportKey reportId, int nbEntities) {
		PacketReport report = packetReportRepo.findByKeyIdAndKeyFileName(reportId.getId(),reportId.getFileName());
		report.setPacketsProcessed(report.getPacketsProcessed() + nbEntities);
		log.info("nb packet processed <{}> for file <{}>",report.getPacketsProcessed(),reportId.getFileName());
		packetReportRepo.save(report);
		
	}

	@Override
	public void addNbEntitiesStored(ReportKey reportId, int nbEntities) {
		PacketReport report = packetReportRepo.findByKeyIdAndKeyFileName(reportId.getId(),reportId.getFileName());
		report.setPacketsStored(report.getPacketsStored() + nbEntities);
		log.info("nb packet stored <{}> for file <{}>",report.getPacketsStored(),reportId.getFileName());
		packetReportRepo.save(report);
	}
}
