package com.shoppr.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shoppr.common.entity.Product;
import com.shoppr.common.exception.ProductNotFoundException;

@Service
@Transactional
public class ProductService {

	@Autowired
	private ProductRepository repo;
	
	public final static Integer ITEMS_PER_PAGE = 5;
	
	public Product get(Integer id) throws ProductNotFoundException {
		try {
			return repo.findById(id).get();
		}catch(NoSuchElementException e) {
			throw new ProductNotFoundException("Product with ID: " + id + "does not exist");
		}
	}
	
	public List<Product> listAll(){
		return (List<Product>) repo.findAll();
	}
	
	public Page<Product> listByPage(int pageNumber, String sortField, String sortDir, 
									String keyword, Integer categoryId){
		
		Pageable pageable = null;
		if(!sortDir.isEmpty()) {
			Sort sort = Sort.by(sortField);
			
			sort = sortDir.equals("asc")? sort.ascending() : sort.descending();
			pageable = PageRequest.of(pageNumber-1, ITEMS_PER_PAGE, sort);
		}
		
		else {
			pageable = PageRequest.of(pageNumber-1, ITEMS_PER_PAGE);
		}
		
		if(keyword != null && !keyword.isEmpty()) {
			
			if(categoryId != null && categoryId > 0) {
				String categoryChildren = "-" + String.valueOf(categoryId) + "-";
				return repo.searchByCategory(categoryId, categoryChildren, keyword, pageable);
			}
			
			return repo.findAll(keyword, pageable);
		}
		
		if(categoryId != null && categoryId > 0) {
			String categoryChildren = "-" + String.valueOf(categoryId) + "-";
			return repo.findAllInCategory(categoryId, categoryChildren, pageable);
		}

		return repo.findAll(pageable);
	}
	
	public Product save(Product product) {
		if(product.getId() == null) {
			product.setCreatedTime(new Date());
		}
		
		if(product.getAlias() == null || product.getAlias().isEmpty()) {
			String defaultAlias = product.getName().replaceAll(" ", "-");
			product.setAlias(defaultAlias);
		}
		else {
			product.setAlias(product.getAlias().replaceAll(" ", "-"));
		}
		
		product.setUpdatedTime(new Date());
		
		return repo.save(product);
	}
	
	public boolean isNameUnique(Integer id, String name) {
		Product product = repo.getProductByName(name);
		
		if(product == null) return true;
		
		boolean isCreatingNew = (id == null);
		
		if(isCreatingNew) {
			if(product != null) return false;
		}
		else {
			if(product.getId() != id) return false;
		}
		
		return true;
	}
	
	public void updateProductEnabledStatus(Integer id, boolean enabled) {
		repo.updateEnabledStatus(id, enabled);
	}
	
	public void delete (Integer id) throws ProductNotFoundException {
		Long countById = repo.countById(id);
		
		if(countById == null || countById == 0)
			throw new ProductNotFoundException("Could not find any product with ID: " + id);
		
		repo.deleteById(id);
	}
	
	public void saveSalespersonUpdate(Product productInForm) {
		
		Product product = repo.findById(productInForm.getId()).get();
		
		product.setCost(productInForm.getCost());
		product.setPrice(productInForm.getPrice());
		product.setDiscountPercent(productInForm.getDiscountPercent());
		product.setUpdatedTime(new Date());
		
		repo.save(product);
	}
}
