package com.oculus.task;

import java.io.File;
import java.util.List;

public interface IntegrationService<I,E> {
	
	
	/**
	 * Process reading file
	 * @param file the file to process
	 * @param reportId report id
	 * @param entities list of entities to fill from the file
	 * @return number of entities been readed. No matter if the entities was wrong(format..) or else
	 * @throws IntegrationException
	 */
	int processRead(File file, I reportId, List<E> entities) throws IntegrationException;

	/**
	 * Process write entities
	 * @param reportId
	 * @param entities
	 * @return number of entities been stored.
	 * @throws IntegrationException when stored failed
	 */
	int processWrite(I reportId, List<E> entities) throws IntegrationException;
	
	/**
	 * check file format
	 * @param file input file to be processed
	 * @throws IntegrationException if file doesn't respect format
	 */
	void checkFile(File file) throws IntegrationException;

	/**
	 * In this task we support only REMOVE&ADD. No UPDATE from last integration are supported
	 * @param file new file to be processed
	 * @return number of entities deleted
	 */
	int clearLastReport(File file);

}
