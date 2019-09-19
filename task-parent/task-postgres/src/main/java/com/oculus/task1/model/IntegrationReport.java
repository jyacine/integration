package com.oculus.task1.model;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "report")
@Data
@Table(name = "report")
@NoArgsConstructor
public class IntegrationReport {
	
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(
		name = "UUID",
		strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
	
	private String filename;
	
	@Temporal(TemporalType.TIMESTAMP)
    private Date started;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date finish;
	
	private int linesProcessed;
	
	private int linesStored;
	
	private int linesFiltered;
	
	@Column
	@ElementCollection(targetClass=IntegrationLineError.class)
	@JoinColumn(name="integration_id", referencedColumnName="id")
	@OneToMany(cascade = {CascadeType.ALL},fetch=FetchType.LAZY)
	private Set<IntegrationLineError> linesError;

	private String status;

}
