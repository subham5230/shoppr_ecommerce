package com.shoppr.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.shoppr.admin.user.UserRepository;
import com.shoppr.common.entity.User;

public class ShopprUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepo;
	

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	
		User user = userRepo.getUserByEmail(email);
		
		if(user != null) {
			return new ShopprUserDetails(user);
		}
		
		throw new UsernameNotFoundException("This email is not registered.");
	}

}
