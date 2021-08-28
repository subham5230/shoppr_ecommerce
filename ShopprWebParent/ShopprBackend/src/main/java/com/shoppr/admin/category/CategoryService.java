package com.shoppr.admin.category;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shoppr.common.entity.Category;
import com.shoppr.common.exception.CategoryNotFoundException;

@Service
@Transactional
public class CategoryService {

	public final static Integer ITEMS_PER_PAGE = 4;
	
	@Autowired
	private CategoryRepository repo;
	
	public List<Category> listAll(){
		return (List<Category>) repo.findAll();
	}
	
	public List<Category> listCategoriesUsedInForm(){
		List<Category> categoriesList = new ArrayList<>();
		
		Iterable<Category> categories = repo.findAll();
		
		for(Category category : categories) {
			if(category.getParent() == null) {
				categoriesList.add(Category.copyIdAndName(category));
				
				Set<Category> children = category.getChildren();
				
				for(Category subCategory : children) {
					String name = "--" + subCategory.getName();
					categoriesList.add(Category.copyIdAndName(subCategory.getId(), name));
					
					printChildren(subCategory, 1, categoriesList);
				}
			}
		}
		
		return categoriesList;
	}
	
	
	private void printChildren(Category parent, int subLevel, List<Category> categoriesList) {
		
		int level = subLevel + 1;
		Set<Category> children = parent.getChildren();
		
		for(Category subCategory : children) {
			String name = "";
			for(int i =0; i < level; i++)
				name += "--";
			
			name += subCategory.getName();
			categoriesList.add(Category.copyIdAndName(subCategory.getId(), name));
			
			printChildren(subCategory, level, categoriesList);
		}
	}

	
	public Page<Category> listByPage(int pageNumber, String sortField, String sortDir, String keyword){
		
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
	
	public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
		repo.updateEnabledStatus(id, enabled);
	}
	
	public void delete (Integer id) throws CategoryNotFoundException {
		Long countById = repo.countById(id);
		
		if(countById == null || countById == 0)
			throw new CategoryNotFoundException("Could not find any category with ID: " + id);
		
		repo.deleteById(id);
	}
	
	public Category save(Category category) {
	
		Category parent = category.getParent();
		if(parent != null) {
			String allParentIds = parent.getAllParentIDs() == null? "-" : parent.getAllParentIDs();
			allParentIds += String.valueOf(parent.getId()) + "-";
			category.setAllParentIDs(allParentIds);
		}
		
		return repo.save(category);
	}
	
	public Category get(Integer id) throws CategoryNotFoundException {
		try {
			return repo.findById(id).get();
		}catch(NoSuchElementException e) {
			throw new CategoryNotFoundException("Could not find any category with ID: " + id);
		}
	}
	
	public boolean isNameUnique(Integer id, String name) {
		Category category = repo.getCategoryByName(name);
		
		if(category == null) return true;
		
		boolean isCreatingNew = (id == null);
		
		if(isCreatingNew) {
			if(category != null) return false;
		}
		else {
			if(category.getId() != id) return false;
		}
		
		return true;
	}
}
