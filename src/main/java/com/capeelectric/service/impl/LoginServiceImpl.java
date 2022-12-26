package com.capeelectric.service.impl;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.capeelectric.config.JwtTokenUtil;
import com.capeelectric.exception.ForgotPasswordException;
import com.capeelectric.exception.RegistrationException;
import com.capeelectric.exception.UpdatePasswordException;
import com.capeelectric.model.RegisterDetails;
import com.capeelectric.model.RegisterationMeter;
import com.capeelectric.repository.RegistrationRepository;
import com.capeelectric.request.AuthenticationRequest;
import com.capeelectric.request.RefreshTokenRequest;
import com.capeelectric.response.AuthenticationResponseRegister;
import com.capeelectric.service.RegistrationService;

/**
 * 
 * @author capeelectricsoftware
 *
 */
@Service
public class LoginServiceImpl {

	private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

	@Autowired
	private JwtTokenUtil jwtProvider;

	@Autowired
	private RefreshTokenService refreshTokenService;

	@Autowired
	private RegistrationRepository registrationRepository;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public AuthenticationResponseRegister refreshToken(RefreshTokenRequest refreshTokenRequest,
			RegistrationDetailsServiceImpl registrationServiceImpl) {
		logger.debug("Refresh Token Starts");
		refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
		final RegisterDetails registerDetails = registrationServiceImpl
				.loadUserByUsername(refreshTokenRequest.getUsername());
		String token = jwtProvider.generateToken(registerDetails);
		return AuthenticationResponseRegister.builder().jwttoken(token)
				.refreshToken(refreshTokenRequest.getRefreshToken())
				.expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMs()))
				.registerationMeter(registerDetails.getRegister()).build();
	}

	public String findByUserNameOrContactNumber(String details)
			throws ForgotPasswordException, IOException, RegistrationException {

		logger.debug("Find By User Name Starts");
		if (details != null && details.contains("@")) {
			Optional<RegisterationMeter> registerRepo = registrationRepository.findByUsername(details);
			return registrationService.sendSMS(registerRepo.get().getContactNumber());
		} else if (details != null && details.length() > 0) {
			Optional<RegisterationMeter> registerRepo = registrationRepository.findByContactNumber(details);
			return registrationService.sendSMS(registerRepo.get().getContactNumber());
		} else {
			logger.error("Email/Contact Number is required");
			throw new ForgotPasswordException("Email/Contact Number is required");
		}

	}

	public RegisterationMeter createPassword(AuthenticationRequest request) throws UpdatePasswordException {
		logger.debug("createPassword Starts");
		if (request.getEmail() != null && request.getPassword() != null) {
			RegisterationMeter register = registrationRepository.findByUsername(request.getEmail()).get();
			if (register != null && register.getUsername().equalsIgnoreCase(request.getEmail())) {
				register.setPassword(passwordEncoder.encode(request.getPassword()));
				register.setUpdatedDate(LocalDateTime.now());
				register.setUpdatedBy(request.getEmail());
				logger.debug("createPassword Ends");
				return registrationRepository.save(register);
			} else {
				logger.error("createPassword Ends");
				throw new UpdatePasswordException("User Not available");
			}
		} else {
			logger.error("createPassword Ends");
			throw new UsernameNotFoundException("Username not valid");
		}
	}

}
