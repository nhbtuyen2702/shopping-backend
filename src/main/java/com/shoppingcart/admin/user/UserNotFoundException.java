package com.shoppingcart.admin.user;

public class UserNotFoundException extends Exception {//exception do mình tự tạo

	private static final long serialVersionUID = 1L;

	public UserNotFoundException(String message) {
		super(message);
	}

}
