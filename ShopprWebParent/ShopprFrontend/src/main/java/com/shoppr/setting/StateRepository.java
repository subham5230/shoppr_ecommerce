package com.shoppr.setting;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.shoppr.common.entity.Country;
import com.shoppr.common.entity.State;

public interface StateRepository extends CrudRepository<State, Integer> {
	
	public List<State> findByCountryOrderByNameAsc(Country country);
}
