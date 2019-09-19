package com.oculus.task2.model;

import java.util.Date;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table
@AllArgsConstructor(access=AccessLevel.PUBLIC)
@NoArgsConstructor
@Data
public class PacketReport {
	
	@PrimaryKeyColumn(
	      name = "report_key", 
	      ordinal = 2, 
	      type = PrimaryKeyType.PARTITIONED, 
	      ordering = Ordering.DESCENDING)
	private ReportKey key;

	private Date started;
	
	private int packetsProcessed;
	
	private int packetsStored;
    
    private Date finish;
	
	private String status;

}
