package com.shoppr.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
	
	@Test
	public void testEncodePassword() {
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		
		String rawPass = "Subham77";
		String encodedPass = passwordEncoder.encode(rawPass);
		
		System.out.println(encodedPass);
		
		boolean matches = passwordEncoder.matches(rawPass, encodedPass);
		
		assertThat(matches).isTrue();
	}

}
