package com.capeelectric.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
/**
 * 
 * @author capeelectricsoftware
 *
 */
public class RegisterDetails extends RegisterationMeter implements UserDetails {

	private static final long serialVersionUID = 1L;

	private RegisterationMeter registerationMeter;

	public RegisterationMeter getRegister() {
		return registerationMeter;
	}

	public void setRegister(RegisterationMeter registerationMeter) {
		this.registerationMeter = registerationMeter;
	}

	public RegisterDetails() {
		super();
	}

	public RegisterDetails(RegisterationMeter registerationMeter) {
		this.registerationMeter = registerationMeter;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		return Arrays.stream(registerationMeter.getRole().split(",")).map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return registerationMeter.getPassword();
	}

	@Override
	public String getUsername() {
		return registerationMeter.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
