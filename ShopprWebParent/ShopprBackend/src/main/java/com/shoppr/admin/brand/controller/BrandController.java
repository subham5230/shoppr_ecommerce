package com.shoppr.admin.brand.controller;

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
import com.shoppr.admin.brand.BrandNotFoundException;
import com.shoppr.admin.brand.BrandService;
import com.shoppr.admin.category.CategoryService;
import com.shoppr.common.entity.Brand;
import com.shoppr.common.entity.Category;

@Controller
public class BrandController {
	
	@Autowired
	private BrandService service;
	
	@Autowired
	private CategoryService categoryService;
	
	
	@GetMapping("/brands")
	public String getBrandsList(Model model) {

		return "redirect:/brands/page/1?sortField=&sortDir=&keyword=";
	}
	
	@GetMapping("/brands/page/{pageNum}")
	public String listByPage(@PathVariable(name="pageNum") int pageNum, Model model,
			@Param("sortField") String sortField, 
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword) {
		
		if(sortDir.isEmpty())
			sortField="";
		
		if(pageNum <= 0) {
			model.addAttribute("keyword", keyword);
			model.addAttribute("totalItems", 0);
			return "/brands/brands";
		}
		
		List<Brand> listBrands = null;
		Page<Brand> page = service.listByPage(pageNum, sortField, sortDir, keyword);
		
		if(pageNum > page.getTotalPages())
			return "redirect:/brands/page/" + page.getTotalPages() + "?sortField=" + sortField + "&sortDir=" + sortDir
					+ "&keyword=" + keyword;
		else
			listBrands = page.getContent();
			
		long startCount  = (pageNum - 1) * BrandService.ITEMS_PER_PAGE + 1;
		long endCount = startCount + BrandService.ITEMS_PER_PAGE - 1;
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
		model.addAttribute("pageTitle", "Manage Brands");
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		
		return "/brands/brands";
	}
	
	@GetMapping("/brands/new")
	public String newBrand(Model model) {
		
		Brand brand = new Brand();
		
		List<Category> listCategoriesForm = categoryService.listCategoriesUsedInForm();
		
		model.addAttribute("brand", brand);
		model.addAttribute("listCategoriesForm", listCategoriesForm);
		model.addAttribute("pageTitle", "Create Brand");
		
		return "/brands/brand_form";
	}
	
	@GetMapping("/brands/delete/{id}")
	public String deleteBrand(@PathVariable(name = "id") Integer id, 
			RedirectAttributes redirectAttributes,
			Model model,
			@Param("currentPage") String currentPage,
			@Param("sortField") String sortField, 
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword) {
		

		try {
			
			String dir = "../brand-logos";
			try {
				FileUploadUtil.deleteFolder(dir, id);
			} catch (IOException e) {
				
			}
			
			Brand brand = service.get(id);
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "Brand: " + brand.getName() + " has been removed.");
			
		}catch(BrandNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			
		}
		
		return "redirect:/brands/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir
				+ "&keyword=" + keyword;
	}
	
	@GetMapping("/brands/edit/{id}")
	public String editCategory(@PathVariable(name = "id") Integer id, 
			RedirectAttributes redirectAttributes,
			Model model) {
		try {
			Brand brand = service.get(id);
			List<Category> listCategoriesForm = categoryService.listCategoriesUsedInForm();
			
			model.addAttribute("brand", brand);
			model.addAttribute("listCategoriesForm", listCategoriesForm);
			model.addAttribute("pageTitle", "Edit Brand");
			
			return "/brands/brand_form";
			
		}catch(BrandNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			
			return "redirect:/brands";
		}
	}
	
	
	@PostMapping("/brands/save")
	public String saveBrand(Brand brand, RedirectAttributes redirectAttributes,
							@RequestParam("profile-image") MultipartFile multipartFile) throws IOException {
		Integer id = brand.getId();

		if(!multipartFile.isEmpty()) {
			
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			brand.setLogo(fileName);
			Brand savedBrand = service.save(brand);
			
			String uploadDir = "../brand-logos/" + savedBrand.getId();
			
			try {
				FileUploadUtil.cleanDir(uploadDir);
			} catch (IOException e) {
				
			}
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}
		else {
			service.save(brand);
		}

		
		if(id == null)
			redirectAttributes.addFlashAttribute("message", "Brand created successfully.");
		
		else
			redirectAttributes.addFlashAttribute("message", "Brand updated successfully.");
		
		String key = brand.getName();
		
		return "redirect:/brands/page/1?sortField=&sortDir=&keyword=" + key ;
	}

}
