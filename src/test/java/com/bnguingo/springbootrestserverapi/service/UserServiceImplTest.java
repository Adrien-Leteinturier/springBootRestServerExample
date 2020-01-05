package com.bnguingo.springbootrestserverapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import static org.mockito.Matchers.any;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import static org.mockito.Mockito.verify;
import com.bnguingo.springbootrestserverapi.dao.UserRepository;
import com.bnguingo.springbootrestserverapi.exception.BusinessResourceException;
import com.bnguingo.springbootrestserverapi.model.Role;
import com.bnguingo.springbootrestserverapi.model.User;
import com.fasterxml.jackson.datatype.jdk8.OptionalLongDeserializer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(SpringRunner.class)
public class UserServiceImplTest {
	
	@TestConfiguration //création des beans nécessaires pour les tests
    static class UserServiceImplTestContextConfiguration {
        
        @Bean//bean de service
        public UserService userService () {
            return new UserServiceImpl();
        }
        
        @Bean//nécessaire pour hacher le mot de passe sinon échec des tests
        public BCryptPasswordEncoder passwordEncoder() {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            return bCryptPasswordEncoder;
        }
    }
	
	@Autowired
	private UserService userService;
	
	@MockBean // creation d'un mockBean pour UserRepository
	private UserRepository userRepository;
	
	User user = new User(1L, "Dupont", "password", 1);
	
	@Test
	public void testFindAllUsers() throws Exception {
		User user = new User("Dupont", "password", 1);
        Role role = new Role("USER_ROLE");//initialisation du role utilisateur
        
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);         
        List<User> allUsers = new ArrayList<User>();
        Mockito.when(userRepository.findAll()).thenReturn(allUsers);
        Collection<User> users = userService.getAllUsers();
        assertNotNull(users);
        assertEquals(users, allUsers);
        assertEquals(users.size(), allUsers.size());
        verify(userRepository).findAll();
	}
	
	@Test 
	public void testGetUSerByLogin() {
		Mockito.when(userRepository.findByLogin("Dupont")).thenReturn(user);
		userService.findByLogin("Dupont");
		verify(userRepository).findByLogin("Dupont");
	}
	
	@Test(expected = BusinessResourceException.class)
	public void testGetUSerByLogin_throw_BusinessResourceException() {
        Mockito.doThrow(new BusinessResourceException(null))
        .when(userRepository).findByLogin(user.getLogin()); 
        userService.findByLogin("Dupont");
        verify(userRepository).findByLogin("Dupont");
	}
	
	
	@Test(expected = BusinessResourceException.class)
	public void testGetUSerById_throw_BusinessResourceException() {
        Mockito.doThrow(new BusinessResourceException(null))
        .when(userRepository).findById(user.getId()); 
        userService.getUserById(1L);
        verify(userRepository).findById(1L).orElse(null);
	}
	
	
	@Test
    public void testSaveUser() throws BusinessResourceException {
        
        //on creer un user que le service de sauvegarde va retourner
        User userMock = new User(1L,"Dupont", "password", 1);
        
        //on check en sauvegardant notre user que le service repository return bien notre userMock
        Mockito.when(userRepository.save((user))).thenReturn(userMock);
        
        //on verifie 
        User userSaved = userService.saveOrUpdateUser(user);
        assertNotNull(userSaved);
        assertEquals(userMock.getId(), userSaved.getId());
         assertEquals(userMock.getLogin(), userSaved.getLogin());
         verify(userRepository).save(any(User.class));
    }
	
	@Test(expected = BusinessResourceException.class)
    public void testSaveUser_throw_BusinessException() throws BusinessResourceException {
        Mockito.when(userRepository.save((user))).thenThrow(new BusinessResourceException(null));
        //on verifie 
        userService.saveOrUpdateUser(user);
    }
	
	@Test
    public void testDelete() throws BusinessResourceException {
        User userMock = new User(1L,"Dupont", "password", 1);
        Mockito.when(userRepository.save((user))).thenReturn(userMock);
        User userSaved = userService.saveOrUpdateUser(user);
        assertNotNull(userSaved);
        assertEquals(userMock.getId(), userSaved.getId());
        userService.deleteUser(userSaved.getId());
        verify(userRepository).deleteById(any(Long.class));
    }
	
	@Test(expected = BusinessResourceException.class)
    public void testDeleteUser_throw_BusinessException() throws BusinessResourceException {
        Mockito.doThrow(new BusinessResourceException(null))
        .when(userRepository).deleteById(user.getId()); 
        userService.deleteUser(user.getId());
    }

    @Test
    public void testUpdateUser() throws BusinessResourceException {
        User userUpdated = new User(1L,"Paul", "password", 1);
        Mockito.when(userRepository.save((user))).thenReturn(userUpdated);
        User userFromDB = userService.saveOrUpdateUser(user);
        assertNotNull(userFromDB);
        assertEquals(userUpdated.getLogin(), userFromDB.getLogin());
        verify(userRepository).save(any(User.class));        
    }    
 
}
