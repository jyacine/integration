package com.oculus.task2.model;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@PrimaryKeyClass
public class PacketKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8748964648050842721L;
	
	@PrimaryKeyColumn(name = "file_name", type = PrimaryKeyType.PARTITIONED)
	private String fileName;

	@PrimaryKeyColumn(name = "packet_id", ordinal = 1, ordering = Ordering.DESCENDING)
	private UUID id;
	
	
}
