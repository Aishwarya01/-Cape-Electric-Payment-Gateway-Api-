package com.capeelectric.service;

import com.capeelectric.model.BuyRentMeter;

public interface PaymentService {

	public void addPaymentDetails(BuyRentMeter cutomer);
	
	public void updatePaymentStatus(String status,String orderId);
}
