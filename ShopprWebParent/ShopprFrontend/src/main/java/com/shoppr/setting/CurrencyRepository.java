package com.shoppr.setting;

import org.springframework.data.repository.CrudRepository;

import com.shoppr.common.entity.Currency;

public interface CurrencyRepository extends CrudRepository<Currency, Integer> {

}
