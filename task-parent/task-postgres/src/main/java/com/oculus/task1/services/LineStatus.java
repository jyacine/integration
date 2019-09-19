package com.oculus.task1.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access=AccessLevel.PUBLIC)
public class LineStatus {
	private int line;	
	private ErrorStatus status;
}
