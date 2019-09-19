package com.oculus.task2.model;

import java.util.Date;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
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
public class PacketEntity {
	
	@PrimaryKeyColumn(
	      name = "packet_key", 
	      ordinal = 2, 
	      type = PrimaryKeyType.PARTITIONED, 
	      ordering = Ordering.DESCENDING)
	private PacketKey key;
	
	@Column
	private String Protocol;
	
	@Column
	private String fullAdressDest;
	
	@Column
	private String fullAdressSrc;
	
	@Column
	private String macAdressDest;
	
	@Column
	private String macAdressSrc;
	
	@Column
	private Date arrivalTime;
	
	@Column
	private boolean isPush;
	
	@Column
	private String fileName;
}
