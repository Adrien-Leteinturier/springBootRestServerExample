package com.bnguingo.springbootrestserverapi.service;

import java.util.Collection;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bnguingo.springbootrestserverapi.dao.UserRepository;
import com.bnguingo.springbootrestserverapi.exception.BusinessResourceException;
import com.bnguingo.springbootrestserverapi.model.User;

@Service(value = "userService")// l'annotation @Service est optionnelle ici, car il n'existe qu'une seule implémentation
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public Collection<User> getAllUsers() {
		return IteratorUtils.toList(userRepository.findAll().iterator());
	}

	@Override
	public User getUserById(Long id) throws BusinessResourceException{
		try {
			return userRepository.findById(id).orElse(null);
		} catch (Exception e) {
			throw new BusinessResourceException("Get user by Id User error", "Erreur de recherche par l'identifiant de l'utilisateur", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public User findByLogin(String login) throws BusinessResourceException {
		try {
			User userFound = userRepository.findByLogin(login);
			return userFound;
		} catch (Exception e) {
			throw new BusinessResourceException("Find By Login", "Erreur de recherche par Login de l'utilisateur", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	@Transactional(readOnly=false)
	public User saveOrUpdateUser(User user){
		try {
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			return userRepository.save(user);
		} catch (Exception e) {
			throw new BusinessResourceException("Create or Update User error", "Erreur de mise a jour ou de sauvegarde de l'utilisateur", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	@Transactional(readOnly=false)
	public void deleteUser(Long id) throws BusinessResourceException {
		try {
			userRepository.deleteById(id);
		} catch (Exception e) {
			throw new BusinessResourceException("Delete User error", "Erreur de suppression de l'utilisateur", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
