package com.shoppr.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shoppr.common.entity.Product;
import com.shoppr.common.exception.ProductNotFoundException;

@Service
public class ProductService {

	public static final Integer PRODUCTS_PER_PAGE = 9;
	
	@Autowired
	private ProductRepository repo;
	
	public Page<Product> listByCategory(Integer pageNum, Integer categoryId){
		String categoryIDMatch = "-" + String.valueOf(categoryId) + "-";
		
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
		
		return repo.listByCategory(categoryId, categoryIDMatch, pageable);
	}
	
	public Product getProduct(String alias) throws ProductNotFoundException {
		Product product = repo.findByAlias(alias);
		
		if(product == null) {
			throw new ProductNotFoundException("Could not find any product with alias: " + alias);
		}
		
		return product;
	}
	
	public Page<Product> search(String keyword, int pageNum){
		
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
		
		return repo.search(keyword, pageable);
	}
}
