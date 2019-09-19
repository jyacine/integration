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
public class ReportKey implements Serializable {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PrimaryKeyColumn(name = "file_name", type = PrimaryKeyType.PARTITIONED)
	private String fileName;

	@PrimaryKeyColumn(name = "report_id", ordinal = 1, ordering = Ordering.DESCENDING)
	private UUID id;


	@Override
	public int hashCode(){
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof ReportKey && o != null){
			ReportKey or = (ReportKey)o;
			return this.fileName.equals(or.fileName) 
					&& this.id.equals(or.id);
		}
		return false;
	}
}