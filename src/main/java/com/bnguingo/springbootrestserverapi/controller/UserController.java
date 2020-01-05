package com.bnguingo.springbootrestserverapi.controller;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bnguingo.springbootrestserverapi.exception.BusinessResourceException;
import com.bnguingo.springbootrestserverapi.model.Role;
import com.bnguingo.springbootrestserverapi.model.User;
import com.bnguingo.springbootrestserverapi.service.RoleService;
import com.bnguingo.springbootrestserverapi.service.UserService;

@Controller
@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
@RequestMapping("/user/*")
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@GetMapping(value = "/users") 
	public ResponseEntity<Collection<User>> getAllUsers() {
		
		Collection<User> users = userService.getAllUsers(); 
		logger.info("liste des utilisateurs : " + users.toString());
		return new ResponseEntity<Collection<User>>(users, HttpStatus.FOUND);

	}
	
	@PostMapping(value = "/users")
	@Transactional
	public ResponseEntity<User> saveUser(@RequestBody User user) {
		
		User userExist = userService.findByLogin(user.getLogin());
        if (userExist != null) {
            logger.debug("L'utilisateur avec le login " + user.getLogin() + " existe déjà");
            throw new BusinessResourceException("Duplicate Login", "Erreur de création ou de mise à jour de l'utilisateur: "+user.getLogin(),HttpStatus.CONFLICT);
        } 
        
		Set<Role> roles= new HashSet<>();
		Role roleUser = new Role("ROLE_USER");
		
		//initialisation du rôle ROLE_USER. 
		
		roles.add(roleUser);
		user.setRoles(roles);
		user.setActive(0);
		Set<Role> roleFromDB = extractRole_Java8(user.getRoles(), roleService.getAllRolesStream());
		user.getRoles().removeAll(user.getRoles()); 
		user.setRoles(roleFromDB);
		User userSave = userService.saveOrUpdateUser(user);
		logger.info("userSave : " + userSave.toString());
		return new ResponseEntity<User>(user, HttpStatus.CREATED);
	}
	
	
	private Set<Role> extractRole_Java8(Set<Role> rolesSetFromUser, Stream<Role> roleStreamFromDB) {
		// Collect UI role names. 
		Set<String> uiRoleNames = rolesSetFromUser.stream()
				.map(Role::getRoleName)
				.collect(Collectors.toCollection(HashSet::new));
		// Filter DB roles. 
		return roleStreamFromDB
				.filter(role -> uiRoleNames.contains(role.getRoleName()))
				.collect(Collectors.toSet());
	}
	
	private Set<Role> extractRoleUsingCompareTo_Java8(Set<Role> rolesSetFromUser, Stream<Role> roleStreamFromDB) { 
		return roleStreamFromDB
				.filter(roleFromDB -> rolesSetFromUser.stream()
						.anyMatch( roleFromUser -> roleFromUser.compareTo(roleFromDB) == 0))
				.collect(Collectors.toCollection(HashSet::new));
	}
	
	private Set<Role> extractRole_BeforeJava8(Set<Role> rolesSetFromUser, Collection<Role> rolesFromDB) {
		Set<Role> rolesToAdd = new HashSet<>();
		for(Role roleFromUser:rolesSetFromUser){ 
			for(Role roleFromDB:rolesFromDB){
				if(roleFromDB.compareTo(roleFromUser)==0){ 
					rolesToAdd.add(roleFromDB);
					break; 
				}	
			}
		}
		return rolesToAdd;
	}
	
	@GetMapping(value = "/users/{loginName}")
	public ResponseEntity<User> findUserByLogin(@PathVariable("loginName") String login) {
		User user = userService.findByLogin(login);
		logger.debug("Utilisateur trouvé : " + user);
		return new ResponseEntity<User>(user, HttpStatus.FOUND);
	}
	
	@PutMapping(value="/users/{id}")
	public ResponseEntity<User> updateUser(@PathVariable(value="id") Long id, @RequestBody User user){
		
		User userToUpdate = userService.getUserById(id);
		if(userToUpdate == null) {
			logger.debug("L'utilisateur avec l'identifiant" + id + " n'existe pas");
			return new ResponseEntity<User>(user, HttpStatus.NOT_FOUND);
		}
		
		logger.info("UPDATED USER :" + userToUpdate.getRoles());
		userToUpdate.setPassword(user.getPassword());
		userToUpdate.setLogin(user.getLogin());
		userToUpdate.setActive(user.getActive());
		User userUpdated = userService.saveOrUpdateUser(userToUpdate);
		return new ResponseEntity<User>(userUpdated, HttpStatus.OK);
	}
	
	@DeleteMapping(value="/users/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable(value="id") Long id){
		
		User user = userService.getUserById(id);
        if (user == null) {
        logger.debug("L'utilisateur avec l'identifiant " + id + " n'existe pas");
        throw new BusinessResourceException("User Not Found","Aucun utilisateur n'existe avec l'identifiant: "+id ,HttpStatus.NOT_FOUND);
        } 
		userService.deleteUser(id);
		return new ResponseEntity<Void>(HttpStatus.GONE);
	}

}
