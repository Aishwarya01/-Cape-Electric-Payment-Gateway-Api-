package com.capeelectric.service.impl;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capeelectric.config.JwtTokenUtil;
import com.capeelectric.model.RegisterDetails;
import com.capeelectric.request.RefreshTokenRequest;
import com.capeelectric.response.AuthenticationResponseRegister;

/**
 * 
 * @author capeelectricsoftware
 *
 */
@Service
public class LoginServiceImpl{

	private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

	@Autowired
	private JwtTokenUtil jwtProvider;

	@Autowired
	private RefreshTokenService refreshTokenService;

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

}
