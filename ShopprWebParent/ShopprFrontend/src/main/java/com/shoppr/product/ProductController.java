package com.shoppr.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shoppr.category.CategoryService;
import com.shoppr.common.entity.Category;
import com.shoppr.common.entity.Product;
import com.shoppr.common.exception.CategoryNotFoundException;
import com.shoppr.common.exception.ProductNotFoundException;

@Controller
public class ProductController {

	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductService productService;
	
	@GetMapping("/c/{category_alias}")
	public String viewCategoryFirstPage(@PathVariable("category_alias") String alias,
			Model model) {
		
		return viewCategoryByPage(alias, 1, model);
	}
	
	@GetMapping("/c/{category_alias}/page/{pageNum}")
	public String viewCategoryByPage(@PathVariable("category_alias") String alias,
			@PathVariable("pageNum") Integer pageNum,
			Model model) {
		
		
		try {
			Category category = categoryService.getCategory(alias);
			
			List<Category> listCategoryParents = categoryService.getCategoryParents(category);
			
			if(category == null) return "error/404";
			
			if(pageNum <= 0) {
				
				model.addAttribute("totalItems", 0);
				model.addAttribute("category", category);
				
				model.addAttribute("pageTitle", category.getName());
				model.addAttribute("listCategoryParents", listCategoryParents);
				return "product/products_by_category";
			}
			
			
			Page<Product> pageProducts = productService.listByCategory(pageNum,  category.getId());
			
			List<Product> listProducts = null;
			
			if(pageNum > pageProducts.getTotalPages())
				return "redirect:/c/"+ alias + "/page/" + pageProducts.getTotalPages();
			else
				listProducts = pageProducts.getContent();
			
			
			long startCount  = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
			long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
			if(endCount > pageProducts.getTotalElements())
				endCount = pageProducts.getTotalElements();
	
			
		
			model.addAttribute("startCount", startCount);
			model.addAttribute("endCount", endCount);
			model.addAttribute("totalItems", pageProducts.getTotalElements());
			model.addAttribute("totalPages", pageProducts.getTotalPages());
			model.addAttribute("currentPage", pageNum);
			model.addAttribute("listProducts", listProducts);
			model.addAttribute("category", category);
			
			model.addAttribute("pageTitle", category.getName());
			model.addAttribute("listCategoryParents", listCategoryParents);
			
			return "product/products_by_category";
		}
		catch(CategoryNotFoundException e) {
			return "error/404";
		}
	}
	
	
	@GetMapping("/p/{product_alias}")
	public String viewCategoryByPage(@PathVariable("product_alias") String alias,
			Model model) {
		
		try {
			Product product = productService.getProduct(alias);
			List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());
			
			model.addAttribute("product", product);
			model.addAttribute("listCategoryParents", listCategoryParents);
			model.addAttribute("pageTitle", product.getShortName());
			
			return "product/product_detail";
		}
		catch(ProductNotFoundException e) {
			return "error/404";
		}
	}
	
	@GetMapping("/search")
	public String searchFirstPage(@Param("keyword") String keyword,
			Model model) {
		
		return searchByPage(keyword, 1, model);
	}
	
	@GetMapping("/search/page/{pageNum}")
	public String searchByPage(@Param("keyword") String keyword,
			@PathVariable("pageNum") Integer pageNum,
			Model model) {
		
		if(pageNum <= 0) {
			
			model.addAttribute("totalItems", 0);
			model.addAttribute("keyword", keyword);
			model.addAttribute("pageTitle", "Search result for '" + keyword + "'");
			
			return "product/search_result";
		}
		
		Page<Product> pageProducts = productService.search(keyword, pageNum);
		List<Product> listResult = null;
		
		
		if(pageNum > pageProducts.getTotalPages())
			return "redirect:/search/page/" + pageProducts.getTotalPages() + "?keyword=" + keyword;
		else
			listResult = pageProducts.getContent();
		
		long startCount  = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
		if(endCount > pageProducts.getTotalElements())
			endCount = pageProducts.getTotalElements();

		
	
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageProducts.getTotalElements());
		model.addAttribute("totalPages", pageProducts.getTotalPages());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("pageTitle", "Search result for '" + keyword + "'");

		model.addAttribute("keyword", keyword);
		model.addAttribute("listProducts", listResult);
		
		return "product/search_result";
	}
}
