package com.oculus.task;

import java.io.File;

public interface ReportService<I> {

	/**
	 * create a new report
	 * @param file
	 * @return reportID
	 */
	I createNewReport(File file);
	
	/**
	 * End integration
	 * @param reportId
	 * @param isSucceed
	 */
	void endIntegration(I reportId, boolean isSucceed);
	
	/**
	 * Add number of entities processed at the report
	 * @param reportId
	 * @param nbEntities
	 */
	void addNbEntitiesProcessed(I reportId, int nbEntities);
	
	/**
	 * Add number of entities stored in db at the report
	 * @param reportId
	 * @param nbEntities
	 */
	void addNbEntitiesStored(I reportId, int nbEntities);
}
