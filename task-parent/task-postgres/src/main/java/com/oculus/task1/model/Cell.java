package com.oculus.task1.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.oculus.task1.model.enums.Radio;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor(access=AccessLevel.PUBLIC)
@NoArgsConstructor
public class Cell {
	
	@Id
	@GeneratedValue
    private Long id;
	
	private UUID integrationId;
	
	@Enumerated(EnumType.STRING)
	private Radio radio;
	
    private int mcc;
    
    private int net;
    
    private int area;
    
    private int cell;
    
    private int unit;
    
    private float lon;
    
    private float lat;
    
    private int range;
    
    private int samples;
    
    private int changeable;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
    
    private int averageSignal;
}
