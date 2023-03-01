package com.udacity.jdnd.course3.critter.repository;

import com.udacity.jdnd.course3.critter.entity.CustomerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<CustomerEntity,Long> {
}
