package com.capeelectric.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.capeelectric.config.OtpConfig;
import com.capeelectric.exception.RegistrationException;
import com.capeelectric.model.RegisterationMeter;
import com.capeelectric.repository.RegistrationRepository;
import com.capeelectric.request.AuthenticationRequest;
import com.capeelectric.service.RegistrationService;

/**
 * 
 * @author capeelectricsoftware
 *
 */
@Service
public class RegistrationServiceImpl implements RegistrationService {

	private static final Logger logger = LoggerFactory.getLogger(RegistrationServiceImpl.class);

	@Autowired
	private RegistrationRepository registerRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private OtpConfig otpConfig;
	
	private static final String SESSION_TITLE = ".*\"Details\":\"(.+)\".*";

	@Override
	public RegisterationMeter addRegistration(RegisterationMeter registerationMeter) throws RegistrationException {
		if (null != registerationMeter && null !=registerationMeter.getUsername()) {
			logger.debug("AddingRegistration Starts with User : {} ", registerationMeter.getUsername());
			Optional<RegisterationMeter> registerRepo = registerRepository
					.findByUsername(registerationMeter.getUsername());
			Optional<RegisterationMeter> registerRepo1 = registerRepository
					.findBycontactNumber(registerationMeter.getContactNumber());
			
			if (!registerRepo.isPresent()&&!registerRepo1.isPresent()) {
				registerationMeter.setPassword(passwordEncoder.encode(registerationMeter.getPassword()));
				registerationMeter.setRole("user");
				registerationMeter.setCreatedDate(LocalDateTime.now());
				registerationMeter.setUpdatedDate(LocalDateTime.now());
				registerationMeter.setCreatedBy(registerationMeter.getUsername());
				registerationMeter.setUpdatedBy(registerationMeter.getUsername());
				RegisterationMeter createdRegister = registerRepository.save(registerationMeter);
				logger.debug("Successfully Registration Information Saved");
				return createdRegister;
			} else {
				logger.error("Given UserName Already Present");
				throw new RegistrationException("Given UserName Already Present");
			}

		} else {
			logger.error("AddingRegistration is Faild , Because Invalid Inputs");
			throw new RegistrationException("Invalid Inputs");
		}
	}

	@Override
	public Optional<RegisterationMeter> retrieveRegistration(String userName) throws RegistrationException {
		if (userName != null) {
			logger.debug("RetrieveRegistration Started with User : {} ", userName);
			
			Optional<RegisterationMeter> registerRepo = registerRepository.findByUsername(userName);
			Optional<RegisterationMeter> registerRepo1 = registerRepository.findBycontactNumber(userName);
			if(registerRepo.isPresent()&& registerRepo.get().getUsername()!=null) {
				return registerRepo;
			}else if(registerRepo1.isPresent()&& registerRepo1.get().getContactNumber()!=null) {
				return registerRepo;
			}
			else {
				throw new RegistrationException("UserName Not Exist");
				
			}
		} else {
			logger.error("RetrieveRegistration is Failed , Because Invalid Inputs");
			throw new RegistrationException("Invalid UserName");
		}

	}

	@Override
	public void updateMobileNumber(String mobilenumber, String userName) throws RegistrationException {

		if (null != mobilenumber && null != userName) {
			Optional<RegisterationMeter> registerRepo = registerRepository.findByUsername(userName);
			if (registerRepo.isPresent() ) {
				registerRepo.get().setContactNumber(mobilenumber);
				logger.debug("Successfully Registration Information updated");
				registerRepository.save(registerRepo.get());
			}
		} else {
			logger.error("Invalid Inputs");
			throw new RegistrationException("Invalid Inputs");
		}

	}

	@Override
	public String verifyOtpForSavingContactNumber(AuthenticationRequest request) throws Exception {

		if (request.getOtp() != null && request.getOtpSession() != null) {

			logger.debug("RegistrationService otpSend() function called =[{}]", "Cape-Electric-SMS-Api");
			ResponseEntity<String> otpVerifyResponse = restTemplate.exchange(
					otpConfig.getVerifyOtp() + request.getOtpSession() + "/" + request.getOtp(), HttpMethod.GET, null,
					String.class);
			logger.debug("Cape-Electric-SMS-Api service OTP_verify Response [{}]", otpVerifyResponse);

			if (!otpVerifyResponse.getBody().matches("(.*)Success(.*)")) {
				logger.error("Cape-Electric-SMS-Api service call failed [{}]" + otpVerifyResponse.getBody());
 				return "OTP MisMatched";
			} else {
 				logger.debug("Given OTP matched:{}", request.getOtp());
				return "OTP Matched";
			}

		} else {
			logger.error("Invalid Inputs");
			throw new Exception("Invalid Inputs");
		}

 	}
	
	@Override
	public String sendSMS(String mobileNumber) throws RegistrationException {
		

		if (isValidMobileNumber(mobileNumber)) {
			logger.debug("RegistrationService otpSend() function called [{}]", "Cape-Electric-SMS-Api");
			ResponseEntity<String> sendOtpResponse = restTemplate.exchange(otpConfig.getSendOtp() + mobileNumber,
					HttpMethod.GET, null, String.class);

			logger.debug("Cape-Electric-SMS-Api service Response=[{}]", sendOtpResponse);

			if (!sendOtpResponse.getBody().matches("(.*)Success(.*)")) {
				logger.error("Cape-Electric-SMS-Api service call failed=[{}]" + sendOtpResponse.getBody());
				throw new RegistrationException(sendOtpResponse.getBody());
			}
			return sendOtpResponse.getBody().replaceAll(SESSION_TITLE, "$1");
		}
		else {
			logger.error("Contact Number is Invalid");
			throw new RegistrationException("Contact Number is Invalid");
		}
		
	}
	
	private boolean isValidMobileNumber(String mobileNumber) {
		Pattern p = Pattern
				.compile("^(\\+\\d{1,3}( )?)?(\\s*[\\-]\\s*)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$");
		Matcher m = p.matcher(mobileNumber);
		return (m.find() && m.group().equals(mobileNumber));
	}
	

}
