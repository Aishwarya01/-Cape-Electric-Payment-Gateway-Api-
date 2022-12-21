package com.capeelectric.service;

import java.util.Optional;

import com.capeelectric.exception.RegistrationException;
import com.capeelectric.model.RegisterationMeter;
import com.capeelectric.request.AuthenticationRequest;

/**
 * 
 * @author capeelectricsoftware
 *
 */
public interface RegistrationService {

	public RegisterationMeter addRegistration(RegisterationMeter registerationMeter) throws RegistrationException;

	public Optional<RegisterationMeter> retrieveRegistration(String userName) throws RegistrationException;

	public void updateMobileNumber(String mobilenumber,String userName) throws RegistrationException;

	public String sendSMS(String mobileNumber) throws RegistrationException;
	
	public String sendSMSUsername(String userName) throws RegistrationException;

	public String verifyOtpForSavingContactNumber(AuthenticationRequest request) throws Exception;
}
