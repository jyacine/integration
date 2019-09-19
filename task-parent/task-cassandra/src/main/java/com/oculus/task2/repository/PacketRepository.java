package com.oculus.task2.repository;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.oculus.task2.model.PacketEntity;

@Repository
public interface PacketRepository extends CassandraRepository<PacketEntity, Long> {

	List<PacketEntity> findByKeyFileName(String fileName);

	List<PacketEntity> deleteByKeyFileName(String name);
}
