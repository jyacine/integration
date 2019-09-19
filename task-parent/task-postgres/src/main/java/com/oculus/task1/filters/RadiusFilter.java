package com.oculus.task1.filters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Filter according to the radius
 * @author Yacine Jaber
 *
 */
@Component
public class RadiusFilter implements Filter {

	@Value("${oculus.task1.filter.radius}")
	private int radius;
	
	@Value("${oculus.task1.filter.latRef}")
	private double latRef;
	
	@Value("${oculus.task1.filter.lonRef}")
	private double lonRef;
	
	@Override
	public boolean proceed(String line) {
		return getRadiusFromLine(line) < radius;
	}

	private double getRadiusFromLine(String line){
		String[] lineSplit = line.split(",");
		double lon = Double.parseDouble(lineSplit[6]);
		double lat = Double.parseDouble(lineSplit[7]);
		
		return 6371 * Math.acos(
		        Math.sin(latRef) * Math.sin(lat)
		        + Math.cos(latRef) * Math.cos(lat) * Math.cos(lon - lonRef));
		
	}
}
