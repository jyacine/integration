package com.oculus.task1.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oculus.task.IntegrationException;
import com.oculus.task.IntegrationService;
import com.oculus.task1.filters.Filter;
import com.oculus.task1.model.Cell;
import com.oculus.task1.model.IntegrationReport;
import com.oculus.task1.model.enums.Radio;

/**
 * public Service for the services integration
 * @author yacine jaber
 *
 */
@Service
public class CellIntegrationService implements IntegrationService<UUID, String> {
	

	@Autowired
	private CellReportService reportService;
	
	@Autowired
	private CellService cellService;
	
	@Autowired
	private List<Filter> filters;
	
	public static final String regex  = "\\D+,(-?\\d*\\.?\\d+?,){10}"
			+ "(\\d{10},){2}\\d";
	public static final String firstLine = "radio,mcc,net,area,cell,unit,lon,lat,range,samples,changeable,created,updated,averageSignal";
	private static final Pattern pattern = Pattern.compile(regex);
	
	private static final Logger log = LoggerFactory.getLogger(CellIntegrationService.class);

	/**
	 * integrate the cells file into the database
	 * @param file file to be processed
	 * @throws IntegrationException 
	 */
	@Override
	public int processRead(File file, UUID reportId, List<String> listBuffers) throws IntegrationException {		
		log.info("start processing file <{}>",file.getAbsoluteFile());		
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new IntegrationException(e.getMessage());
		}
		Scanner sc = new Scanner(inputStream, "UTF-8");
		if(sc.hasNextLine()){
			if(!sc.nextLine().equals(firstLine)){
				throw new IntegrationException("Error format incorrect");
			}
		}
		int lineIndex = 0;
		int linesFiltered = 0;
		try{
			while(sc.hasNextLine()){
				String line = sc.nextLine();
				final Matcher matcher = pattern.matcher(line);
				//check format
				if(matcher.matches() && matcher.replaceAll("").equals("")){
					log.debug("line format correct. Index <{}> , line <{}>",lineIndex,line);
					//check line within the filters
					if(checkFilters(line)){
						listBuffers.add(line);
					}else{
						linesFiltered++;
						log.debug("line index <{}> filtered. line <{}>",lineIndex,line);
					}
				}else{
					reportService.addLineWrongFormat(reportId, line);
					log.warn("line index <{}> doesn't match. line <{}>",lineIndex,line);
				}

				if(linesFiltered >= 100000){
					reportService.addLineFiltered(reportId, linesFiltered);
					linesFiltered = 0;
				}
				lineIndex++;
			}
			reportService.addLineFiltered(reportId, linesFiltered);
		}catch(Exception ex){
			throw new IntegrationException(ex.getMessage());
		}finally {
			sc.close();
		}
		log.info("file reader <{}> processing ended succefully",reportId);
		return lineIndex;
	}

	@Override
	public int processWrite(UUID reportId, List<String> lines) throws IntegrationException {
		if(lines.size() > 0){
			List<Cell> listCells = getCellsFromLines(lines,reportId);
			if(!cellService.save(listCells)){
				log.error("failed to insert lines <{}> ",listCells.size());
				reportService.addLineStoreFailed(reportId, lines);
				return listCells.size();
			}
			log.info("db writer <{}> processing ended succefully. nb lines processed <{}>",reportId,lines.size());
		}
		return 0;
	}
	
	@Override
	public void checkFile(File file) throws IntegrationException {
		Optional<String> extFile = getExtensionByStringHandling(file.getName());
		if(!extFile.isPresent() || !extFile.get().equals("csv")){
			throw new IntegrationException("file not present or format error");
		}
	}
	
	private boolean checkFilters(String line){
		for (Filter filter : filters) {
			if(filter.proceed(line)){
				return false;
			}
		}
		return true;
	}
	
	private Optional<String> getExtensionByStringHandling(String filename) {
	    return Optional.ofNullable(filename)
	      .filter(f -> f.contains("."))
	      .map(f -> f.substring(filename.lastIndexOf(".") + 1));
	}
	
	private List<Cell> getCellsFromLines(List<String> listLines,UUID reportId){
		List<Cell> cells = new ArrayList<Cell>(listLines.size());
		for (String string : listLines) {
			cells.add(getCellFromLine(string,reportId));
		}
		return cells;
	}
	
	private Cell getCellFromLine(String line,UUID reportId){
		try{
			String[] lineCSV = line.split(",");
			Radio radio = Radio.valueOf(lineCSV[0]);
		    int mcc = Integer.valueOf(lineCSV[1]);
		    int net = Integer.valueOf(lineCSV[2]);
		    int area = Integer.valueOf(lineCSV[3]);
		    int cell = Integer.valueOf(lineCSV[4]);
		    int unit = Integer.valueOf(lineCSV[5]);
		    float lon = Float.valueOf(lineCSV[6]);
		    float lat = Float.valueOf(lineCSV[7]);
		    int range = Integer.valueOf(lineCSV[8]);
		    int samples = Integer.valueOf(lineCSV[9]);
		    int changeable = Integer.valueOf(lineCSV[10]);
		    Date created = new Date(Integer.valueOf(lineCSV[11]));
		    Date updated = new Date(Integer.valueOf(lineCSV[12]));
		    int averageSignal = Integer.valueOf(lineCSV[13]);
		    
		    return new Cell(null,reportId,radio, mcc, net, area, cell, unit, 
		    		lon, lat, range, samples, changeable, 
		    		created, updated, averageSignal);
		}catch (Exception e) {
			log.error("line format error, line <{}>",line);
		}
		return null;	
	}

	@Override
	public int clearLastReport(File file) {
		IntegrationReport report = reportService.findReportByFilename(file.getName());
		if(report != null){
			return cellService.deleteCellsByReport(report.getId());
		}
		return 0;
	}
}
