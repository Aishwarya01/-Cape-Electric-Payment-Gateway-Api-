package com.capeelectric.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.capeelectric.model.Customer;

@Repository
public interface PaymentRepo extends CrudRepository<Customer, Integer> {

	public Optional<Customer> findByOrderId(String orderId);

}
