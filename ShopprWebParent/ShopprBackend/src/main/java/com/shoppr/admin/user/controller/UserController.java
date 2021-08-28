package com.shoppr.admin.user.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shoppr.admin.FileUploadUtil;
import com.shoppr.admin.user.UserNotFoundException;
import com.shoppr.admin.user.UserService;
import com.shoppr.common.entity.Role;
import com.shoppr.common.entity.User;

@Controller
public class UserController {

	@Autowired
	private UserService service;
	
	@GetMapping("/users")
	public String listAll(Model model) {

		return "redirect:/users/page/1?sortField=&sortDir=&keyword=";
	} 
	
	
	@GetMapping("/users/page/{pageNum}")
	public String listByPage(@PathVariable(name="pageNum") int pageNum, Model model,
			@Param("sortField") String sortField, 
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword) {
		
		if(sortDir.isEmpty())
			sortField="";
		
		if(sortField.equals("roles"))
			keyword="";
		
		if(pageNum <= 0) {
			model.addAttribute("keyword", keyword);
			model.addAttribute("totalItems", 0);
			return "/users/users";
		}
		
		Page<User> page = service.listByPage(pageNum, sortField, sortDir, keyword);
		List<User> listUsers = null;
		
		if(pageNum > page.getTotalPages())
			return "redirect:/users/page/" + page.getTotalPages() + "?sortField=" + sortField + "&sortDir=" + sortDir
					+ "&keyword=" + keyword;
		else
			listUsers = page.getContent();
		
		long startCount  = (pageNum - 1) * UserService.USERS_PER_PAGE + 1;
		long endCount = startCount + UserService.USERS_PER_PAGE - 1;
		if(endCount > page.getTotalElements())
			endCount = page.getTotalElements();

		String reverseSortDir = null;

		if(sortDir.equals("asc")) {
			reverseSortDir = "desc";
		}
		if(sortDir.equals("desc")) {
			reverseSortDir = "";
		}
		if(sortDir.isEmpty()) {
			reverseSortDir = "asc";
		}
	
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("listUsers", listUsers);
		model.addAttribute("pageTitle", "Manage Users");
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		
		return "/users/users";
	}
	
	@GetMapping("/users/new")
	public String newUser(Model model) {
		
		List<Role> listRoles = service.listRoles();
		
		User user = new User();
		user.setEnabled(true);
		
		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "Create User");
		
		return "/users/user_form";
	}
	
	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
							@RequestParam("profile-image") MultipartFile multipartFile) throws IOException {
		Integer id = user.getId();
		if(!multipartFile.isEmpty()) {
			
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = service.save(user);
			
			String uploadDir = "user-photos/" + savedUser.getId();
			
			try {
				FileUploadUtil.cleanDir(uploadDir);
			} catch (IOException e) {
				
			}
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}
		else {
			if(user.getPhotos().isEmpty()) user.setPhotos(null);
			service.save(user);
		}
		
		if(id == null)
			redirectAttributes.addFlashAttribute("message", "User created successfully.");
		
		else
			redirectAttributes.addFlashAttribute("message", "User updated successfully.");
		
		String key = user.getEmail().split("@")[0];
		
		return "redirect:/users/page/1?sortField=&sortDir=&keyword=" + key ;
	}
	
	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, 
			RedirectAttributes redirectAttributes,
			Model model) {
		try {
			User user = service.get(id);
			List<Role> listRoles = service.listRoles();
			
			model.addAttribute("user", user);
			model.addAttribute("listRoles", listRoles);
			model.addAttribute("pageTitle", "Edit User");
			
			return "/users/user_form";
			
		}catch(UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			
			return "redirect:/users";
		}
	}
	
	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, 
			RedirectAttributes redirectAttributes,
			Model model,
			@Param("currentPage") String currentPage,
			@Param("sortField") String sortField, 
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword) {
;
		try {
			
			String dir = "user-photos";
			try {
				FileUploadUtil.deleteFolder(dir, id);
			} catch (IOException e) {
				
			}
			
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "User: " + id + " has been removed.");
			
		}catch(UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			
		}
		
		return "redirect:/users/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir
				+ "&keyword=" + keyword;
	}
	
	@GetMapping("/users/{id}/enabled/{enabled}")
	public String updateUserEnabledStatus(@PathVariable(name="id") Integer id,
											@PathVariable(name="enabled") boolean enabled
											,RedirectAttributes redirectAttributes,
											@Param("currentPage") String currentPage,
											@Param("sortField") String sortField, 
											@Param("sortDir") String sortDir,
											@Param("keyword") String keyword) {
		
		service.updateUserEnabledStatus(id, enabled);
		String status = enabled? "enabled" : "disabled";
		String message = "The user ID: "+ id + " has been " + status;
		
		redirectAttributes.addFlashAttribute("message", message);
		
		return "redirect:/users/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir
				+ "&keyword=" + keyword;
	}
}
