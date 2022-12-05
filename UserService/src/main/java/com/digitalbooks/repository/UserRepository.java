package com.digitalbooks.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digitalbooks.model.User;
@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	Optional<User> findByUserName(String userName);
	
	Boolean existsByUserName(String UserName);
	Boolean existsByemailId(String emailId);

	Boolean existsByPhoneNumber(String phoneNumber);

}
