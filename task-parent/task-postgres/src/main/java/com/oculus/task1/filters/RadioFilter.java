package com.oculus.task1.filters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.oculus.task1.model.enums.Radio;

/**
 * Filter according to the radio
 * @author Yacine Jaber
 *
 */
@Component
public class RadioFilter implements Filter {

	@Value("${oculus.task1.filter.radio}")
	private Radio radio;
	
	@Override
	public boolean proceed(String line) {
		return !getRadioFromLine(line).equals(radio);
	}

	private Radio getRadioFromLine(String line){
		String[] lineSplit = line.split(",");
		return Radio.valueOf(lineSplit[0]);
	}
	
}
