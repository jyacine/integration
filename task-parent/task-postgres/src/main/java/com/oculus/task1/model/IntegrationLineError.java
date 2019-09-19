package com.oculus.task1.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.oculus.task1.model.enums.ErrorLine;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor(access=AccessLevel.PUBLIC)
@NoArgsConstructor
@Table(name="integration_lines_error")
public class IntegrationLineError {
	
	@Id
	@GeneratedValue
    private Long id;
	
	@Column(name="integration_id")
	private UUID integrationId;
	
	private String line;
	
	@Enumerated(EnumType.STRING)
	private ErrorLine error;
	

}
