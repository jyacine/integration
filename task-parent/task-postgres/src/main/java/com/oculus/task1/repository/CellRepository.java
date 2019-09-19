package com.oculus.task1.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.oculus.task1.model.Cell;

public interface CellRepository extends CrudRepository<Cell, Long> {

	List<Cell> removeByIntegrationId(UUID id);
}
