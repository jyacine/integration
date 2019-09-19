package com.oculus.task1.controler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oculus.task1.model.IntegrationReport;
import com.oculus.task1.services.CellReportService;
import com.oculus.task1.services.CellService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(value = "Report Controller")
public class ReportController {

	@Autowired
	CellReportService reportService;
	
	@Autowired
	CellService cellService;
	
	@ApiOperation(value = "Get last report")
	@GetMapping("/report/lastreport")
	public IntegrationReport getLastReport() {
		return reportService.getLastReport();
	}
	
	@ApiOperation(value = "Get report by Id")
	@GetMapping("/report/{reportId}")
	public IntegrationReport findById(@ApiParam("reportId") @PathVariable("reportId") UUID reportId) {
		return reportService.getRecordById(reportId);
	}
	
	@ApiOperation(value = "Get report by date")
	@GetMapping("/report/byDateStart")
	public List<IntegrationReport> getByStartedTimeBetween(
			@ApiParam(name = "startDate", value = "yyyy-MM-dd", defaultValue = "1970-01-01") 
			@RequestParam("startDate") String dateBegin,
			@ApiParam(name = "endDate", value = "yyyy-MM-dd", defaultValue = "")
			@RequestParam("endDate") String dateEnd) throws ParseException{
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return reportService.getByStartedTimeBetween(sdf.parse(dateBegin), sdf.parse(dateEnd));
	}
	
}
