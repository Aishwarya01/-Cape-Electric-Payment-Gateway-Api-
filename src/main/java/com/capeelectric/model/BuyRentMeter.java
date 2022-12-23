package com.capeelectric.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author CAPE-SOFTWARE
 *
 */
@Entity
@Table(name = "rent_meter_table")
public class BuyRentMeter implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "CUSTOMER_ID")
	private Integer customerId;
	
	@Column(name = "ORDER_ID")
	private String orderId;

	@Column(name = "CUSTOMER_EMAIL")
	private String customerEmail;
	
	@Column(name = "METER_NAME")
	private String meterName;
	
	@Column(name = "NUMBER_OF_METER")
	private Integer numberOfMeter;

	@Column(name = "CUSTOMER_PHONENUMBER")
	private String customerPhoneNumber;

	@Column(name = "ADDRESS")
	private String shippingAddress;

	@Column(name = "DISTRICT")
	private String district;

	@Column(name = "COUNTRY")
	private String country;

	@Column(name = "PINCODE")
	private String pinCode;

	@Column(name = "STATE")
	private String state;

	@Column(name = "AMOUNT")
	private String amount;

	@Column(name = "ORDER_STATUS")
	private String orderStatus;
	
	@Transient
	private String name;

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getMeterName() {
		return meterName;
	}

	public void setMeterName(String meterName) {
		this.meterName = meterName;
	}

	public String getCustomerPhoneNumber() {
		return customerPhoneNumber;
	}

	public void setCustomerPhoneNumber(String customerPhoneNumber) {
		this.customerPhoneNumber = customerPhoneNumber;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Integer getNumberOfMeter() {
		return numberOfMeter;
	}

	public void setNumberOfMeter(Integer numberOfMeter) {
		this.numberOfMeter = numberOfMeter;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
