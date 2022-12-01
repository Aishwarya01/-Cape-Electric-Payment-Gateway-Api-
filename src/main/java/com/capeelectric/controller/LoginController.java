package com.capeelectric.controller;

import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capeelectric.config.JwtTokenUtil;
import com.capeelectric.exception.AuthenticationException;
import com.capeelectric.exception.RegistrationException;
import com.capeelectric.model.RegisterDetails;
import com.capeelectric.model.RegisterationMeter;
import com.capeelectric.repository.RegistrationRepository;
import com.capeelectric.request.RefreshTokenRequest;
import com.capeelectric.response.AuthenticationResponseRegister;
import com.capeelectric.service.impl.LoginServiceImpl;
import com.capeelectric.service.impl.RefreshTokenService;
import com.capeelectric.service.impl.RegistrationDetailsServiceImpl;
/**
 * 
 * @author capeelectricsoftware
 *
 */
@RestController
@RequestMapping("/api/v1")
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private RegistrationDetailsServiceImpl registrationDetailsServiceImpl;

	@Autowired
	private LoginServiceImpl loginService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private RegistrationRepository registrationRepository;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private RefreshTokenService refreshTokenService;

	@PostMapping("/authenticate")
	public AuthenticationResponseRegister createAuthenticationToken(
			@RequestBody RefreshTokenRequest authenticationRequest)
			throws Exception, AuthenticationException, RegistrationException {
		logger.debug("Create Authenticate Token starts");
		authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());

		final RegisterDetails registerDetails = registrationDetailsServiceImpl
				.loadUserByUsername(authenticationRequest.getEmail());

		final String token = jwtTokenUtil.generateToken(registerDetails);
		logger.debug("Create Authenticate Token ends");
		return AuthenticationResponseRegister.builder().jwttoken(token)
				.refreshToken(refreshTokenService.generateRefreshToken().getToken())
				.expiresAt(Instant.now().plusMillis(jwtTokenUtil.getJwtExpirationInMs()))
				.registerationMeter(registerDetails.getRegister()).build();

	}

	@PostMapping("/refreshToken")
	public AuthenticationResponseRegister refreshTokens(@RequestBody RefreshTokenRequest refreshTokenRequest) {
		return loginService.refreshToken(refreshTokenRequest, registrationDetailsServiceImpl);
	}

	private void authenticate(String username, String password)
			throws Exception, AuthenticationException, RegistrationException {
		Optional<RegisterationMeter> registerRepo = registrationRepository.findByUsername(username);
		if (registerRepo.isPresent()) {
			try {
				authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
				logger.debug("Authentication done sucessfully");
			} catch (DisabledException e) {
				logger.error("Authentication failed : " + e.getMessage());
				throw new DisabledException("USER_DISABLED", e);
			} catch (BadCredentialsException e) {
				logger.error("Authentication failed : " + e.getMessage());
				throw new BadCredentialsException("INVALID_CREDENTIALS", e);
			}

		} else {
			logger.error("There is no registered user available for this email");
			throw new RegistrationException("There is no registered user available for this email");
		}
	}
}
