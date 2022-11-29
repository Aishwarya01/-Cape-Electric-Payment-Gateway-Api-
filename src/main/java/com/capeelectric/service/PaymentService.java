package com.capeelectric.service;

import com.capeelectric.model.Customer;

public interface PaymentService {

	public void addPaymentDetails(Customer cutomer);
	
	public void updatePaymentStatus(String status,String orderId);
}
