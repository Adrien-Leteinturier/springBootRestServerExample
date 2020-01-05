package com.bnguingo.springbootrestserverapi.dao;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bnguingo.springbootrestserverapi.model.User;


public interface UserRepository extends JpaRepository<User, Long>{

	User findByLogin(String login);

}
