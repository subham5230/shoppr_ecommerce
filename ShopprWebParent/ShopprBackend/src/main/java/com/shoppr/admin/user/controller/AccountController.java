package com.shoppr.admin.user.controller;

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

import com.shoppr.admin.FileUploadUtil;
import com.shoppr.admin.security.ShopprUserDetails;
import com.shoppr.admin.user.UserService;
import com.shoppr.common.entity.User;

@Controller
public class AccountController {

	@Autowired
	private UserService service;
	
	@GetMapping("/account")
	public String viewDetails(@AuthenticationPrincipal ShopprUserDetails loggedUser,
			Model model) {
		
		String email = loggedUser.getUsername();
		User user = service.getByEmail(email);
		
		model.addAttribute("id", user.getId());
		model.addAttribute("pageTitle", "View Profile");
		model.addAttribute("user", user);
		
		return "/users/account_form";
	}
	
	@PostMapping("/account/update")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
							@RequestParam("profile-image") MultipartFile multipartFile,
							@AuthenticationPrincipal ShopprUserDetails loggedUser) throws IOException {

		if(!multipartFile.isEmpty()) {
			
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = service.updateAccount(user);
			
			String uploadDir = "user-photos/" + savedUser.getId();
			
			try {
				FileUploadUtil.cleanDir(uploadDir);
			} catch (IOException e) {
				
			}
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}
		else {
			if(user.getPhotos().isEmpty()) user.setPhotos(null);
			service.updateAccount(user);
		}

		loggedUser.setFirstName(user.getFirstName());
		loggedUser.setLastName(user.getLastName());
		redirectAttributes.addFlashAttribute("message", "Your profile has been updated");

		
		return "redirect:/account" ;
	}
}
