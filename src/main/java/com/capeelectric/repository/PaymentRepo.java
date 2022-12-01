package com.capeelectric.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.capeelectric.model.BuyRentMeter;

@Repository
public interface PaymentRepo extends CrudRepository<BuyRentMeter, Integer> {

	public Optional<BuyRentMeter> findByOrderId(String orderId);

}
