package com.bnguingo.springbootrestserverapi.service;

import java.util.Collection;
import com.bnguingo.springbootrestserverapi.exception.BusinessResourceException;
import com.bnguingo.springbootrestserverapi.model.User;

public interface UserService {
	
	Collection<User> getAllUsers();
	
	User findByLogin(String login) throws BusinessResourceException;
	
	User saveOrUpdateUser(User user) throws BusinessResourceException;
	
	void deleteUser(Long id) throws BusinessResourceException;

	User getUserById(Long id) throws BusinessResourceException;

}
