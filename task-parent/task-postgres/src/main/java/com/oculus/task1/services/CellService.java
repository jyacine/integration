package com.oculus.task1.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oculus.task1.model.Cell;
import com.oculus.task1.repository.CellRepository;

@Service
@Transactional
public class CellService {

	@Autowired
	private CellRepository cellRepository;
	
	public boolean save(List<Cell> cells) {
		return cellRepository.saveAll(cells) != null;
	}
	
	public int deleteCellsByReport(UUID id){
		List<Cell> cells = cellRepository.removeByIntegrationId(id);
		return (cells != null) ? cells.size() : 0;
	}

	
	
}
