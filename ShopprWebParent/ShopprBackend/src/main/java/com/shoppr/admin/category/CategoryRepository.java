package com.shoppr.admin.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.shoppr.common.entity.Category;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {
	
	@Query("SELECT u FROM Category u WHERE u.name = :name")
	public Category getCategoryByName(@Param("name") String name);

	public Long countById(Integer id);
	
	@Query("SELECT u FROM Category u WHERE CONCAT(u.id, ' ', u.name, ' ', u.alias)"
			+ "LIKE %?1%")
	public Page<Category> findAll(String keyword, Pageable pageable);
	
	@Query("UPDATE Category u SET u.enabled = ?2 WHERE u.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);

}
