package com.shoppr.admin.brand;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shoppr.common.entity.Brand;

@Service
@Transactional
public class BrandService {
	
	@Autowired
	private BrandRepository repo;
	
	public final static Integer ITEMS_PER_PAGE = 5;
	
	public List<Brand> listAll(){
		return (List<Brand>) repo.findAll();
	}
	
	public Page<Brand> listByPage(int pageNumber, String sortField, String sortDir, String keyword){
		
		Pageable pageable = null;
		if(!sortDir.isEmpty()) {
			Sort sort = Sort.by(sortField);
			
			sort = sortDir.equals("asc")? sort.ascending() : sort.descending();
			pageable = PageRequest.of(pageNumber-1, ITEMS_PER_PAGE, sort);
		}
		
		else {
			pageable = PageRequest.of(pageNumber-1, ITEMS_PER_PAGE);
		}
		
		
		return repo.findAll(keyword, pageable);
	}
	
	public boolean isNameUnique(Integer id, String name) {
		Brand brand = repo.getBrandByName(name);
		
		if(brand == null) return true;
		
		boolean isCreatingNew = (id == null);
		
		if(isCreatingNew) {
			if(brand != null) return false;
		}
		else {
			if(brand.getId() != id) return false;
		}
		
		return true;
	}
	
	public Brand save(Brand brand) {
		return repo.save(brand);
	}

	public Brand get(Integer id) throws BrandNotFoundException {
		try {
			return repo.findById(id).get();
		}catch(NoSuchElementException e) {
			throw new BrandNotFoundException("Could not find any brand with ID: " + id);
		}
	}
	
	public void delete (Integer id) throws BrandNotFoundException {
		Long countById = repo.countById(id);
		
		if(countById == null || countById == 0)
			throw new BrandNotFoundException("Could not find any brand with ID: " + id);
		
		repo.deleteById(id);
	}

}
