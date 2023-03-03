package com.shoppingcart.admin.user;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shoppingcart.admin.FileUploadUtil;
import com.shoppingcart.admin.entity.User;
import com.shoppingcart.admin.security.ShoppingUserDetails;

@Controller
public class AccountController {

	@Autowired
	private UserService service;

	@GetMapping("/account")
	public String viewDetails(@AuthenticationPrincipal ShoppingUserDetails loggedUser, Model model) {//dùng @AuthenticationPrincipal để lấy ra đối tượng ShoppingUserDetails, ShoppingUserDetails sẽ tương ứng với đối tượng principal trên html  
		String email = loggedUser.getUsername();
		User user = service.getByEmail(email);
		model.addAttribute("user", user);

		return "users/account_form";

	}

	@PostMapping("/account/update")
	public String saveDetails(User user, RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal ShoppingUserDetails loggedUser, @RequestParam("image") MultipartFile multipartFile)
			throws IOException {

		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = service.updateAccount(user);

			String uploadDir = "user-photos/" + savedUser.getId();

			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

		} else {
			if (user.getPhotos().isEmpty())
				user.setPhotos(null);
			service.updateAccount(user);
		}

		loggedUser.setFirstName(user.getFirstName());//loggedUser chính là đối tượng principal trên html -->nếu thay đổi loggedUser thì principal cũng thay đối theo
		loggedUser.setLastName(user.getLastName());

		redirectAttributes.addFlashAttribute("message", "Your account details have been updated.");

		return "redirect:/account";
	}
}
