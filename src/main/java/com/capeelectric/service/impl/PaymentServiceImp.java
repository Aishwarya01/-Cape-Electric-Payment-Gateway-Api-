package com.capeelectric.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capeelectric.model.Customer;
import com.capeelectric.repository.PaymentRepo;
import com.capeelectric.service.PaymentService;

@Service
public class PaymentServiceImp implements PaymentService {

	@Autowired
	private PaymentRepo paymentRepo;

	@Override
	public void addPaymentDetails(Customer cutomer) {
		paymentRepo.save(cutomer);
	}

	@Override
	public void updatePaymentStatus(String status, String orderId) {
		Optional<Customer> cutomerRepo = paymentRepo.findByOrderId(orderId);
		if (cutomerRepo.isPresent()) {
			cutomerRepo.get().setOrderStatus(status);
			paymentRepo.save(cutomerRepo.get());
		}
	}

}
