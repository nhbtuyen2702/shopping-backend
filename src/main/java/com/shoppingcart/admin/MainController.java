package com.shoppingcart.admin;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping("") //localhost:8082/ShoppingCartAdmin
	public String viewHomePage() {
		return "index";
	}

	@GetMapping("/login")
	public String viewLoginPage() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();//trường hợp vừa login xong, đang ở trang index, bấm back lại thì nó vẫn giữ ở trang index
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {//nếu authentication == null thì có nghĩa là chưa login -->trả về trang login
			return "login";
		}

		return "redirect:/";
	}
}
