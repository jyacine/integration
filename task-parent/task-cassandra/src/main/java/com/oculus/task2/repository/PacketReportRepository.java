package com.oculus.task2.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.oculus.task2.model.PacketReport;
import com.oculus.task2.model.ReportKey;

@Repository
public interface PacketReportRepository extends CassandraRepository<PacketReport, ReportKey> {

	//to use in test or maintenance
	PacketReport findOneByKeyFileName(String fileName);

	List<PacketReport> deleteByKeyFileName(String name);

	PacketReport findByKeyIdAndKeyFileName(UUID id, String fileName);

}
