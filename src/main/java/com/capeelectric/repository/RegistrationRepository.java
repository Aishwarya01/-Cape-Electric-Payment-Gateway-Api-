package com.capeelectric.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.capeelectric.model.RegisterationMeter;
/**
 * 
 * @author capeelectricsoftware
 *
 */
@Repository
public interface RegistrationRepository extends CrudRepository<RegisterationMeter, Integer> {

	public Optional<RegisterationMeter> findByUsername(String username);

}
