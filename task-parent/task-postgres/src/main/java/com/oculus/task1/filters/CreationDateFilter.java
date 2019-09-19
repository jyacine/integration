package com.oculus.task1.filters;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Filter according to created date
 * @author Yacine Jaber
 *
 */
@Component
public class CreationDateFilter implements Filter {

	@Value("#{new java.text.SimpleDateFormat('${oculus.task1.filter.dateformat}')."
			+ "parse('${oculus.task1.filter.datebegin}')}")
	private Date t0;
	
	@Value("#{new java.text.SimpleDateFormat('${oculus.task1.filter.dateformat}')."
			+ "parse('${oculus.task1.filter.dateend}')}")
	private Date t1;
	
	@Override
	public boolean proceed(String line) {
		Date lineDate = getCreatedDateFromLine(line);
		return !lineDate.after(t0) || !lineDate.before(t1);
	}

	private Date getCreatedDateFromLine(String line){
		String[] lineSplit = line.split(",");
		return new Date(Long.parseLong(lineSplit[12]));	
	}

}
