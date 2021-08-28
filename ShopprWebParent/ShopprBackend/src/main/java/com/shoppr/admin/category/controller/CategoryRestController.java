package com.shoppr.admin.category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.shoppr.admin.category.CategoryService;

@RestController
public class CategoryRestController {

		
	@Autowired
	private CategoryService service;
	
	@PostMapping("/categories/check_name")
	public String checkDuplicateEmail(@Param("id") Integer id, @Param("name") String name) {
		return service.isNameUnique(id, name)? "OK" : "Duplicated";
	}

}
