package com.shoppr.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shoppr.common.entity.Role;
import com.shoppr.common.entity.User;


@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

	@Autowired
	private UserRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateUser() {
		Role role = entityManager.find(Role.class, 1);
		
		User user = new User("subham@gmail.com", "1234", "Subham", "Mohanty");
		user.addRole(role);
		
		User savedUser = repo.save(user);
		
		assertThat(savedUser.getId()).isGreaterThan(0); 
	}
	
	@Test
	public void testCreateAnotherUser() {
		Role role1 = entityManager.find(Role.class, 2);
		Role role2 = entityManager.find(Role.class, 3);
		
		User user = new User("ravi@gmail.com", "1234", "Ravi", "Das");
		user.addRole(role1);
		user.addRole(role2);
		
		User savedUser = repo.save(user);
		
		assertThat(savedUser.getId()).isGreaterThan(0); 
	}
	
	@Test
	public void testListAllUsers() {
		Iterable<User> listUser = repo.findAll();
		listUser.forEach(user -> System.out.println("\n\n"+ user + "\n\n"));
	}
	
	@Test 
	public void findUserById() {
		User user = repo.findById(2).get();
		System.out.println(user);
		
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testUpdateUserDetails() {
		User user = repo.findById(2).get();
		
		user.setEmail("updatedSubham@gmail.com");
		user.setEnabled(true);
		
		repo.save(user);
	}
	
	@Test
	public void testUpdateUserRoles() {
		User user = repo.findById(4).get();
		Role editor = new Role(3);
		Role shipper = new Role(4);	
		
		user.getRoles().remove(editor);
		user.addRole(shipper);
		
		repo.save(user);
		
	}
	
	@Test
	public void testDeleteUser() {
		Integer id = 4;
		repo.deleteById(id);
	
	}
	
	@Test
	public void testGetUserById() {
		String email = "updatedSubham@gmail.com";
		User user = repo.getUserByEmail(email);
		
		assertThat(user).isNotNull();
				
	}
	
	@Test
	public void testCountById() {
		Integer id = 2;
		
		Long count = repo.countById(id);
		
		assertThat(count).isNotNull().isGreaterThan(0);
	}
	
	@Test
	public void testDisableUser() {
		Integer id = 2;
		repo.updateEnabledStatus(id, false);
	}
	
	@Test
	public void testEnableUser() {
		Integer id = 2;
		repo.updateEnabledStatus(id, true);
	}
	
	@Test 
	public void testListFirstPage() {
		int pageNumber = 0;
		int pageSize = 4;
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		
		Page<User> page = repo.findAll(pageable);
		
		List<User> listUsers = page.getContent();
		
		listUsers.forEach(user -> {
			System.out.println(user);
		});
		
		assertThat(listUsers.size() == pageSize);
	}
	
	@Test
	public void testSearchUsers() {
		String keyword = "bruce";
		
		int pageNumber = 0;
		int pageSize = 4;
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		
		Page<User> page = repo.findAll(keyword, pageable);
		
		List<User> listUsers = page.getContent();
		
		listUsers.forEach(user -> {
			System.out.println(user);
		});
		
		assertThat(listUsers.size()).isGreaterThan(0);
	}
	
}
