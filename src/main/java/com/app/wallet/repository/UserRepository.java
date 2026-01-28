package com.app.wallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.wallet.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	// Find user by email (for login)
	Optional<User> findByEmail(String email);
}
