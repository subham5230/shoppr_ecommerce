package com.shoppr.admin.category.controller;

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
import com.shoppr.admin.category.CategoryService;
import com.shoppr.common.entity.Category;
import com.shoppr.common.exception.CategoryNotFoundException;

@Controller
public class CategoryController {
	
	@Autowired
	private CategoryService service;

	@GetMapping("/categories")
	public String getCategoriesList(Model model) {

		return "redirect:/categories/page/1?sortField=&sortDir=&keyword=";
	}
	
	@GetMapping("/categories/page/{pageNum}")
	public String listByPage(@PathVariable(name="pageNum") int pageNum, Model model,
			@Param("sortField") String sortField, 
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword) {
		
		if(sortDir.isEmpty())
			sortField="";
		
		if(pageNum <= 0) {
			model.addAttribute("keyword", keyword);
			model.addAttribute("totalItems", 0);
			return "/categories/categories";
		}
		
		List<Category> listCategories = null;
		Page<Category> page = service.listByPage(pageNum, sortField, sortDir, keyword);
		
		if(pageNum > page.getTotalPages())
			return "redirect:/categories/page/" + page.getTotalPages() + "?sortField=" + sortField + "&sortDir=" + sortDir
					+ "&keyword=" + keyword;
		else
			listCategories = page.getContent();
			
		long startCount  = (pageNum - 1) * CategoryService.ITEMS_PER_PAGE + 1;
		long endCount = startCount + CategoryService.ITEMS_PER_PAGE - 1;
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
		model.addAttribute("pageTitle", "Manage Categories");
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		
		return "/categories/categories";
	}
	
	@GetMapping("/categories/{id}/enabled/{enabled}")
	public String updateUserEnabledStatus(@PathVariable(name="id") Integer id,
											@PathVariable(name="enabled") boolean enabled
											,RedirectAttributes redirectAttributes,
											@Param("currentPage") String currentPage,
											@Param("sortField") String sortField, 
											@Param("sortDir") String sortDir,
											@Param("keyword") String keyword) {
		
		service.updateCategoryEnabledStatus(id, enabled);
		String status = enabled? "enabled" : "disabled";
		String message = "Category ID: "+ id + " has been " + status;
		
		redirectAttributes.addFlashAttribute("message", message);
		
		return "redirect:/categories/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir
				+ "&keyword=" + keyword;
	}
	
	@GetMapping("/categories/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, 
			RedirectAttributes redirectAttributes,
			Model model,
			@Param("currentPage") String currentPage,
			@Param("sortField") String sortField, 
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword) {
		

		try {
			
			String dir = "../category-photos";
			try {
				FileUploadUtil.deleteFolder(dir, id);
			} catch (IOException e) {
				
			}
			
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "Category: " + id + " has been removed.");
			
		}catch(CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			
		}
		
		return "redirect:/categories/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir
				+ "&keyword=" + keyword;
	}
	
	
	@GetMapping("/categories/edit/{id}")
	public String editCategory(@PathVariable(name = "id") Integer id, 
			RedirectAttributes redirectAttributes,
			Model model) {
		try {
			Category category = service.get(id);
			
			List<Category> listCategoriesForm = service.listCategoriesUsedInForm();
			
			model.addAttribute("category", category);
			model.addAttribute("listCategoriesForm", listCategoriesForm);
			model.addAttribute("pageTitle", "Edit Category");
			
			return "/categories/category_form";
			
		}catch(CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			
			return "redirect:/categories";
		}
	}
	
	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		
		Category category = new Category();
		category.setEnabled(true);
		
		List<Category> listCategoriesForm = service.listCategoriesUsedInForm();
		
		model.addAttribute("category", category);
		model.addAttribute("listCategoriesForm", listCategoriesForm);
		model.addAttribute("pageTitle", "Create Category");
		
		return "/categories/category_form";
	}
	
	@PostMapping("/categories/save")
	public String saveCategory(Category category, RedirectAttributes redirectAttributes,
							@RequestParam("profile-image") MultipartFile multipartFile) throws IOException {
		Integer id = category.getId();

		if(!multipartFile.isEmpty()) {
			
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			category.setImage(fileName);
			Category savedCategory = service.save(category);
			
			String uploadDir = "../category-photos/" + savedCategory.getId();
			
			try {
				FileUploadUtil.cleanDir(uploadDir);
			} catch (IOException e) {
				
			}
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}
		else {
			service.save(category);
		}

		
		if(id == null)
			redirectAttributes.addFlashAttribute("message", "Category created successfully.");
		
		else
			redirectAttributes.addFlashAttribute("message", "Category updated successfully.");
		
		String key = "" + category.getId() + " " + category.getName();
		
		return "redirect:/categories/page/1?sortField=&sortDir=&keyword=" + key ;
	}
	
}
