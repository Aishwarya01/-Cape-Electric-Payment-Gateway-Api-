package com.capeelectric.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capeelectric.config.PaymentConfig;
import com.capeelectric.exception.PaymentException;
import com.capeelectric.model.BuyRentMeter;
import com.capeelectric.model.RazorPay;
import com.capeelectric.model.Response;
import com.capeelectric.service.PaymentService;
import com.google.gson.Gson;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

/**
 * @author CAPE-SOFTWARE
 *
 */
@RestController
@RequestMapping("/api/v1")
public class PaymentController {

	@Autowired	
	private PaymentConfig paymentConfig;

	@Autowired	
	private PaymentService paymentService;

	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

	private RazorpayClient client;

	private static Gson gson = new Gson();

	@PutMapping(value = "/createPayment")
	public ResponseEntity<String> createOrder(@RequestBody BuyRentMeter customer)
			throws RazorpayException, PaymentException {
		this.client = new RazorpayClient(paymentConfig.getSecretId(), paymentConfig.getSecretKey());
		try {
			logger.debug("Order creation started");
			Order order = createRazorPayOrder(customer.getAmount());
			RazorPay razorPay = getRazorPay((String) order.get("id"), customer);
			customer.setOrderId(order.get("id"));
			paymentService.addPaymentDetails(customer);
			return new ResponseEntity<String>(gson.toJson(getResponse(razorPay, 200)), HttpStatus.OK);
		} catch (RazorpayException e) {
			logger.debug("Order creation failed" + e.getMessage());
			return new ResponseEntity<String>(gson.toJson(getResponse(new RazorPay(), 500)),
					HttpStatus.EXPECTATION_FAILED);
		}
	}

	@GetMapping(value = "/updatePaymentStatus/{status}/{orderId}")
	public ResponseEntity<Void> updatePaymentStatus(@PathVariable String status, @PathVariable String orderId)
			throws RazorpayException {	
		logger.debug("updatePaymentStatus function started.. OrderId: {}, OrderStatus: {} ", orderId, status);
		paymentService.updatePaymentStatus(status, orderId);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	@GetMapping(value = "/retrivePaymentStatus/{username}")
	public ResponseEntity<List<BuyRentMeter>> retrivePaymentStatus(@PathVariable String username)
			throws RazorpayException {
		logger.debug("retrivePaymentStatus function started.. username: {} ", username);
		return new ResponseEntity<List<BuyRentMeter>>(paymentService.retrivePaymentStatus(username), HttpStatus.OK);
	}

	private Response getResponse(RazorPay razorPay, int statusCode) {
		Response response = new Response();
		response.setStatusCode(statusCode);
		response.setRazorPay(razorPay);
		return response;
	}

	private RazorPay getRazorPay(String orderId, BuyRentMeter customer) {
		RazorPay razorPay = new RazorPay();
//		razorPay.setApplicationFee(convertRupeeToPaise(customer.getAmount()));
		razorPay.setCustomerName(customer.getName());
		razorPay.setCustomerEmail(customer.getCustomerEmail());
		razorPay.setRazorpayOrderId(orderId);	
		razorPay.setSecretKey(paymentConfig.getSecretId());
		razorPay.setNotes("notes" + orderId);
		return razorPay;
	}

	private Order createRazorPayOrder(String amount) throws RazorpayException {

		JSONObject options = new JSONObject();
		options.put("amount",convertRupeeToPaise(amount));
		// TODO need to changes
		options.put("currency", "INR");
//		options.put("receipt", "txn_123456");
//		options.put("payment_capture", 1); // You can enable this if you want to do Auto Capture.
		return client.Orders.create(options);
	}

	private String convertRupeeToPaise(String paise) {
		BigDecimal b = new BigDecimal(paise);
		BigDecimal value = b.multiply(new BigDecimal("100"));
		return value.setScale(0, RoundingMode.UP).toString();

	}
}
