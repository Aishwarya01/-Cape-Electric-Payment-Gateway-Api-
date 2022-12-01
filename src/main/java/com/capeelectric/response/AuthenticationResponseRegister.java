package com.capeelectric.response;

import java.io.Serializable;
import java.time.Instant;

import com.capeelectric.model.RegisterationMeter;

import lombok.Builder;

/**
 * 
 * @author capeelectricsoftware
 *
 */
@Builder
public class AuthenticationResponseRegister implements Serializable {

	private static final long serialVersionUID = -8091879091924046844L;
	private final String jwttoken;
	private final RegisterationMeter registerationMeter;
	private String refreshToken;
	private Instant expiresAt;

	public AuthenticationResponseRegister(String jwttoken, RegisterationMeter registerationMeter) {
		this.jwttoken = jwttoken;
		this.registerationMeter = registerationMeter;
	}

	public AuthenticationResponseRegister(String jwttoken, RegisterationMeter registerationMeter, String refreshToken, Instant expiresAt) {
		super();
		this.jwttoken = jwttoken;
		this.registerationMeter = registerationMeter;
		this.refreshToken = refreshToken;
		this.expiresAt = expiresAt;
	}

	public String getToken() {
		return this.jwttoken;
	}

	public RegisterationMeter getRegister() {
		return registerationMeter;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

}