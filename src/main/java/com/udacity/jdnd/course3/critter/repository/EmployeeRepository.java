package com.udacity.jdnd.course3.critter.repository;

import com.udacity.jdnd.course3.critter.entity.EmployeeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends CrudRepository<EmployeeEntity,Long> {
}
