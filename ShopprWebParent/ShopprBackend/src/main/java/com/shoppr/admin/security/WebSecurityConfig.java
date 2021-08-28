package com.shoppr.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public UserDetailsService userDetailsService() {
		return new ShopprUserDetailsService();
	}
	
	@Bean
	public BCryptPasswordEncoder PasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(PasswordEncoder());
		
		return authProvider;
	}
	
	private String admin = "Admin";
	private String salesperson = "Salesperson";
	private String editor = "Editor";
	private String shipper = "Shipper";
	private String assistant = "Assistant";
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/users/**").hasAnyAuthority(admin)
			.antMatchers("/categories/**").hasAnyAuthority(admin, editor)
			.antMatchers("/brands/**").hasAnyAuthority(admin, editor)
			.antMatchers("/products").hasAnyAuthority(admin, editor, salesperson, shipper)
			.antMatchers("/products/page/**").hasAnyAuthority(admin, editor, salesperson, shipper)
			.antMatchers("/products/detail/**").hasAnyAuthority(admin, editor, salesperson, shipper)
			.antMatchers("/products/new").hasAnyAuthority(admin, editor)
			.antMatchers("/products/edit/**").hasAnyAuthority(admin, editor, salesperson)
			.antMatchers("/products/save").hasAnyAuthority(admin, editor, salesperson)
			.antMatchers("/products/check_name").hasAnyAuthority(admin, editor, salesperson)
			.antMatchers("/products/delete/**").hasAnyAuthority(admin, editor)
			.antMatchers("/questions/**").hasAnyAuthority(admin, assistant)
			.antMatchers("/reviews/**").hasAnyAuthority(admin, assistant)
			.antMatchers("/customers/**").hasAnyAuthority(admin, salesperson)
			.antMatchers("/shipping/**").hasAnyAuthority(admin, salesperson)
			.antMatchers("/orders/**").hasAnyAuthority(admin, salesperson, shipper)
			.antMatchers("/reports/**").hasAnyAuthority(admin, salesperson)
			.antMatchers("/articles/**").hasAnyAuthority(admin, editor)
			.antMatchers("/menus/**").hasAnyAuthority(admin, editor)
			.antMatchers("/settings/**").hasAnyAuthority(admin)
			.anyRequest().authenticated()
			.and()
			.formLogin()
				.loginPage("/login")
				.usernameParameter("email")
				.permitAll()
			.and().logout().permitAll()
			.and().rememberMe()
					.key("asdb&SAHDaSDbA521531gadghaSSAD23123123")
					.tokenValiditySeconds(2*7*24*60*60);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}
	
	

	
}
