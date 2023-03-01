package com.shoppingcart.admin.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController//RestController = @Controller + @ResponseBody -->sẽ trả về dữ liệu(data), ko trả về view
public class UserRestController {

	@Autowired
	private UserService service;

	@PostMapping("/users/check_email")
	public String checkDuplicateEmail(Integer id, String email) {//params = { id: userId, email: userEmail} -->khai báo id,email đúng tên và thứ tự như object params
		return service.isEmailUnique(id, email) ? "OK" : "Duplicated";
	}
}
