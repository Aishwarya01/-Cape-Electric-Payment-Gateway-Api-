package com.capeelectric.controller;

import static org.springframework.http.HttpStatus.OK;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capeelectric.config.JwtTokenUtil;
import com.capeelectric.exception.AuthenticationException;
import com.capeelectric.exception.ForgotPasswordException;
import com.capeelectric.exception.RegistrationException;
import com.capeelectric.exception.UpdatePasswordException;
import com.capeelectric.model.ChangePasswordRequest;
import com.capeelectric.model.RegisterDetails;
import com.capeelectric.model.RegisterationMeter;
import com.capeelectric.repository.RegistrationRepository;
import com.capeelectric.request.AuthenticationRequest;
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
		final RegisterDetails registerDetails;
		if(null != authenticationRequest.getPassword() && null != authenticationRequest.getUsername() ) {
			authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
			registerDetails = registrationDetailsServiceImpl
					.loadUserByUsername(authenticationRequest.getUsername());
			
		}
		else {
		registerDetails = registrationDetailsServiceImpl
				.loadUserByUsername(authenticationRequest.getMobileNumber());
		}
		final String token = jwtTokenUtil.generateToken(registerDetails);
		logger.debug("Create Authenticate Token ends");
		return AuthenticationResponseRegister.builder().jwttoken(token)
				.refreshToken(refreshTokenService.generateRefreshToken().getToken())
				.expiresAt(Instant.now().plusMillis(jwtTokenUtil.getJwtExpirationInMs()))
				.registerationMeter(registerDetails.getRegister()).build();

	}
	
	@PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.status(OK).body("Refresh Token Deleted Successfully!!");
    }

	@PostMapping("/refreshToken")
	public AuthenticationResponseRegister refreshTokens(@RequestBody RefreshTokenRequest refreshTokenRequest) {
		return loginService.refreshToken(refreshTokenRequest, registrationDetailsServiceImpl);
	}

	private void authenticate(String username, String password)
			throws Exception, AuthenticationException, RegistrationException {
		
		Optional<RegisterationMeter> registerRepo = null;
		
		
		     if(username.contains("@")) {
		    	 registerRepo=registrationRepository.findByUsername(username);
		     }
		     else {
		    	 registerRepo=registrationRepository.findByContactNumber(username);
		     }
		     
		if (registerRepo.isPresent()) {
			try {
				authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(registerRepo.get().getUsername(), password));
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
	
	@GetMapping("/forgotPassword/{email}")
	public ResponseEntity<String> forgotPassword(@PathVariable String email)
			throws ForgotPasswordException, IOException, RegistrationException {
		logger.debug("forgotPassword started");
		String sessionKey = loginService.findByUserNameOrContactNumber(email);
		return new ResponseEntity<String>(sessionKey, HttpStatus.OK);

	}

	@PutMapping("/createPassword")
	public ResponseEntity<String> createPassword(@RequestBody AuthenticationRequest request)
			throws UpdatePasswordException, IOException {
		logger.debug("CreatePassword starts");
		loginService.createPassword(request);
		return new ResponseEntity<String>("You have successfully created/modified your password", HttpStatus.OK);

	}
	
	@PutMapping("/changePassword")
	public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request)
			throws IOException, ForgotPasswordException {
		
		logger.debug("Change Password Starts");
		loginService.changePassword(request.getEmail(), request.getOldPassword(),
				request.getPassword());
		return new ResponseEntity<String>("You have successfully changed your password", HttpStatus.OK);
		
	}

}
    
