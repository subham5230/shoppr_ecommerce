package com.shoppr.admin.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.shoppr.common.entity.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

	public Long countById(Integer id);
	
	@Query("SELECT u FROM Product u WHERE u.name = :name")
	public Product getProductByName(@Param("name") String name);
	
	@Query("SELECT u FROM Product u WHERE u.category.id = ?1 "
				+"OR u.category.allParentIDs LIKE %?2% ")
	public Page<Product> findAllInCategory(Integer categoryId, String categoryChildren, Pageable pageable);
	
	@Query("SELECT u FROM Product u WHERE u.name LIKE %?1% "
			+ "OR u.shortDescription LIKE %?1% "
			+ "OR u.fullDescription LIKE %?1% "
			+ "OR u.category.name LIKE %?1% "
			+ "OR u.brand.name LIKE %?1% ")
	public Page<Product> findAll(String keyword, Pageable pageable);
	
	
	@Query("SELECT u FROM Product u WHERE (u.category.id = ?1 "
			+ "OR u.category.allParentIDs LIKE %?2% ) AND "
			+ "(u.name LIKE %?3% "
			+ "OR u.shortDescription LIKE %?3% "
			+ "OR u.fullDescription LIKE %?3% "
			+ "OR u.category.name LIKE %?3% "
			+ "OR u.brand.name LIKE %?3% )")
	public Page<Product> searchByCategory(Integer categoryId, String categoryChildren, String keyword, Pageable pageable);
	
	@Query("UPDATE Product u SET u.enabled = ?2 WHERE u.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);
}
