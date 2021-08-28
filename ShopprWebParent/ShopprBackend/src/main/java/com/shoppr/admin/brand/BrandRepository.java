package com.shoppr.admin.brand;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.shoppr.common.entity.Brand;

public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer> {

	@Query("SELECT u FROM Brand u WHERE u.name = :name")
	public Brand getBrandByName(@Param("name") String name);

	public Long countById(Integer id);
	
	@Query("SELECT u FROM Brand u ORDER BY u.name ASC")
	public List<Brand> findAll();
	
	@Query("SELECT u FROM Brand u WHERE u.name" + " LIKE %?1%")
	public Page<Brand> findAll(String keyword, Pageable pageable);
	
}
