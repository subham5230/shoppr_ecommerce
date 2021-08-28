package com.shoppr.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.shoppr.common.entity.Role;
import com.shoppr.common.entity.User;

@Service
@Transactional
public class UserService {
	public static Integer USERS_PER_PAGE = 4;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	public List<User> listAll(){
		return (List<User>) userRepo.findAll();
	}
	
	public User getByEmail(String email) {
		return userRepo.getUserByEmail(email);
	}
	
	public Page<User> listByPage(int pageNumber, String sortField, String sortDir, String keyword){
		
		Pageable pageable = null;
		if(!sortDir.isEmpty()) {
			Sort sort = Sort.by(sortField);
			
			sort = sortDir.equals("asc")? sort.ascending() : sort.descending();
			pageable = PageRequest.of(pageNumber-1, USERS_PER_PAGE, sort);
		}
		
		else {
			pageable = PageRequest.of(pageNumber-1, USERS_PER_PAGE);
		}
		
		
		return userRepo.findAll(keyword, pageable);
	}
	public List<Role> listRoles(){
		return (List<Role>) roleRepo.findAll();
	}

	public User save(User user) {
		boolean isUpdatingUser = (user.getId() != null);
		
		if(isUpdatingUser) {
			User existingUser = userRepo.findById(user.getId()).get();
			
			if(user.getPassword().isEmpty())
				user.setPassword(existingUser.getPassword());
			else
				encodePassword(user);
		}
		else
			encodePassword(user);
		
		
		return userRepo.save(user);
	}
	
	public User updateAccount(User userInForm) {
		User userInDB = userRepo.findById(userInForm.getId()).get();
		
		if(!userInForm.getPassword().isEmpty()) {
			userInDB.setPassword(userInForm.getPassword());
		}
		
		if(userInForm.getPhotos() != null) {
			userInDB.setPhotos(userInForm.getPhotos());
		}
		
		userInDB.setFirstName(userInForm.getFirstName());
		userInDB.setLastName(userInForm.getLastName());
		
		return userRepo.save(userInDB);
	}
	
	private void encodePassword(User user) {
		String encodedPass = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPass);
	}
	
	public boolean isEmailUnique(Integer id, String email) {
		User user = userRepo.getUserByEmail(email);
		
		if(user == null) return true;
		
		boolean isCreatingNew = (id == null);
		
		if(isCreatingNew) {
			if(user != null) return false;
		}
		else {
			if(user.getId() != id) return false;
		}
		
		return true;
	}

	public User get(Integer id) throws UserNotFoundException {
		try {
			return userRepo.findById(id).get();
		}catch(NoSuchElementException e) {
			throw new UserNotFoundException("Could not find any user with ID: " + id);
		}
	}
	
	public void delete (Integer id) throws UserNotFoundException {
		Long countById = userRepo.countById(id);
		
		if(countById == null || countById == 0)
			throw new UserNotFoundException("Could not find any user with ID: " + id);
		
		userRepo.deleteById(id);
	}
	
	public void updateUserEnabledStatus(Integer id, boolean enabled) {
		userRepo.updateEnabledStatus(id, enabled);
	}
}
