package com.capeelectric.service;

import java.util.List;

import com.capeelectric.exception.PaymentException;
import com.capeelectric.model.BuyRentMeter;

/**
 * 
 * @author capeelectricsoftware
 *
 */
public interface PaymentService {

	public void addPaymentDetails(BuyRentMeter cutomer) throws PaymentException;
	
	public void updatePaymentStatus(String status,String orderId);

	public List<BuyRentMeter> retrivePaymentStatus(String username);
}
