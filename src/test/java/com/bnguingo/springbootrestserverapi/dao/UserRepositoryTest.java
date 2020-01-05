package com.bnguingo.springbootrestserverapi.dao;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;

import com.bnguingo.springbootrestserverapi.model.User;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private UserRepository userRepository;
	
	User user = new User("Dupont", "password", 1);
	
	@Before
	public void setup() {
		entityManager.persist(user); // on sauvegarde l'objet user au debut du test 
		entityManager.flush();
	}
	
	@Test
	public void testFindAllUsers() {
		List<User> users = userRepository.findAll();
		assertThat(users.size(), is(4));
	}
	
	@Test
	public void testSaveUsers() {
		User user = new User("Paul", "password", 1);
		User userSaved = userRepository.save(user);
		
		assertNotNull(userSaved.getId());
		assertThat("Paul", is(userSaved.getLogin()));
	}
	
	@Test
	public void testFindByLogin() {
		User userFromDb = userRepository.findByLogin("user1");
		assertThat("user1", is(userFromDb.getLogin()));
		// user2 a ete creer dans le fichier de config data.sql
	}
	
	@Test
	public void testDeleteUser() {
		userRepository.deleteById(user.getId());
		User userFromDb = userRepository.findByLogin(user.getLogin());
		assertNull(userFromDb);
	}
	
	@Test
	public void testUpdateUser() {
		User useToUpdate = userRepository.findByLogin(user.getLogin());
		useToUpdate.setActive(0);
		User userToUpdate = userRepository.save(useToUpdate);
		User userUpdatedFromDb = userRepository.findByLogin(userToUpdate.getLogin());
		assertNotNull(userUpdatedFromDb);
		assertThat(0, is(userUpdatedFromDb.getActive()));
	}

}
