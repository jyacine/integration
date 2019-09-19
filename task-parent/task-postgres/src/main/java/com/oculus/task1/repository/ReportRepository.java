package com.oculus.task1.repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oculus.task1.model.IntegrationReport;

public interface ReportRepository extends JpaRepository<IntegrationReport, UUID> {

	IntegrationReport findTopByOrderByIdDesc();
	
	List<IntegrationReport> findByStartedBetween(Date startedBegin,
			Date startedEnd);

	IntegrationReport findReportByFilename(String fileName);

}
