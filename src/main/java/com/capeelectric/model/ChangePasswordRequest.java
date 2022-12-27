package com.capeelectric.model;

import com.capeelectric.request.AuthenticationRequest;

public class ChangePasswordRequest extends AuthenticationRequest {
	private String oldPassword;

	public ChangePasswordRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ChangePasswordRequest(String oldPassword) {
		super();
		this.oldPassword = oldPassword;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

}
