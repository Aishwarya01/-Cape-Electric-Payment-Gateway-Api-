package com.capeelectric.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capeelectric.exception.RegistrationException;
import com.capeelectric.model.RegisterationMeter;
import com.capeelectric.request.AuthenticationRequest;
import com.capeelectric.service.RegistrationService;

/**
 * 
 * @author capeelectricsoftware
 *
 */
@RestController
@RequestMapping("/api/v1")
public class RegistrationController {

	private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

	@Autowired
	private RegistrationService registrationService;

	@PostMapping("/addRegistration")
	public ResponseEntity<String> addRegistration(@RequestBody RegisterationMeter registerationMeter)
			throws RegistrationException {
		logger.debug("Called addRegistration function UserName : {}", registerationMeter.getUsername());
		registrationService.addRegistration(registerationMeter);
		return new ResponseEntity<String>("You have Successfully Created Your Account", HttpStatus.OK);
	}

	@GetMapping("/retrieveRegistration/{userName}")
	public Optional<RegisterationMeter> retrieveRegistration(@PathVariable String userName)
			throws RegistrationException {
		logger.debug("called retrieveRegistration function UserName : {}", userName);
		return registrationService.retrieveRegistration(userName);
	}


	
	@PutMapping("/updateMobileNumber/{mobilenumber}/{username}")
	public ResponseEntity<String> updateMobileNumber(@PathVariable String mobilenumber, @PathVariable String username)
			throws RegistrationException {
		logger.debug("updateMobileNumber starts ...");
		registrationService.updateMobileNumber(mobilenumber, username);
		return new ResponseEntity<String>("You have Successfully Updated Your MobileNumber", HttpStatus.OK);

	}
	
	@GetMapping("/sendSMS/{mobileNumber}")
	public ResponseEntity<String> sendSMS(@PathVariable String mobileNumber)
			throws RegistrationException {
		logger.debug("sendingSMS starts !!!");
		return new ResponseEntity<String>(registrationService.sendSMS(mobileNumber), HttpStatus.OK);
		
	}
	
	@GetMapping("/sendSMSUsername/{username}")
	public ResponseEntity<String> sendSMSUsername(@PathVariable String username)
			throws RegistrationException {
		logger.debug("sendingSMS starts !!!");
		return new ResponseEntity<String>(registrationService.sendSMSUsername(username), HttpStatus.OK);
		
	}
	
	@PutMapping("/verifyOTP")
	public ResponseEntity<String> verifyOTP(@RequestBody AuthenticationRequest authenticationRequest)
			throws Exception {
		logger.debug("verifyOTP starts !!!");
		return new ResponseEntity<String>(registrationService.verifyOtpForSavingContactNumber(authenticationRequest), HttpStatus.OK);
		
	}

}
