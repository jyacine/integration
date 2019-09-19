package com.oculus.task1.filters;

public interface Filter {

	/**
	 * Check if the line match the specific filter
	 * @param line input line from csv
	 * @return true if filter match
	 */
	boolean proceed(String line);
}
