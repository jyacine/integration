package com.oculus.task2.controler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.oculus.task2.model.PacketEntity;
import com.oculus.task2.model.PacketReport;
import com.oculus.task2.repository.PacketRepository;
import com.oculus.task2.service.PacketReportService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(value = "Report Controller")
public class ReportController {

	@Autowired
	PacketRepository packetService;
	
	@Autowired
	PacketReportService reportService;
	
	@ApiOperation(value = "Get packet by fileName")
	@GetMapping("/packets/{fileName}/{offset}/{size}")
	public List<PacketEntity> getPacketByFilename(
					@ApiParam("fileName") @PathVariable("fileName") 
					String fileName,
					@ApiParam("list size") @PathVariable("size")
					int size) {
		Pageable pageable = PageRequest.of(0, size);
		Slice<PacketEntity> slice = packetService.findByKeyFileName(fileName,pageable);
		if(slice != null){
			return slice.getContent();
		}
		return null;
	}
	
	@ApiOperation(value = "Get report by fileName")
	@GetMapping("/report/{fileName}")
	public PacketReport getIntegrationReportByFilename(@ApiParam("fileName") @PathVariable("fileName") 
					String fileName) {
		return reportService.findReportByFilename(fileName);
	}
	
}
