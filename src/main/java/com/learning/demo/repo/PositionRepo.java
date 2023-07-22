package com.learning.demo.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.learning.demo.entity.Position;

@Repository
public interface PositionRepo extends CrudRepository<Position, String> {

}
