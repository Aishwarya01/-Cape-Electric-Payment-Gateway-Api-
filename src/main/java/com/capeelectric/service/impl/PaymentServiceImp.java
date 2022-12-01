package com.capeelectric.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capeelectric.exception.PaymentException;
import com.capeelectric.model.BuyRentMeter;
import com.capeelectric.repository.PaymentRepo;
import com.capeelectric.service.PaymentService;

/**
 * 
 * @author capeelectricsoftware
 *
 */
@Service
public class PaymentServiceImp implements PaymentService {

	private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImp.class);

	@Autowired
	private PaymentRepo paymentRepo;

	@Override
	public void addPaymentDetails(BuyRentMeter buyRentMeter) throws PaymentException {
		if (null != buyRentMeter && null != buyRentMeter.getCustomerEmail()) {
			logger.debug("Sucessfully saved meterdetails into DB");
			paymentRepo.save(buyRentMeter);
		} else {
			logger.error("Invalid Input");
			throw new PaymentException("Invalid Input");
		}

	}

	@Override
	public void updatePaymentStatus(String status, String orderId) {
		Optional<BuyRentMeter> cutomerRepo = paymentRepo.findByOrderId(orderId);
		if (cutomerRepo.isPresent()) {
			logger.debug("Sucessfully updated OrderStatus into DB.. OrderStatus [{}]"+status);
			cutomerRepo.get().setOrderStatus(status);
			paymentRepo.save(cutomerRepo.get());
		}
	}

}
