package com.sravan.bank.repository;

import com.sravan.bank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Boolean existsByEmail(String email);

    Boolean existsByAccountNumber(String accountNumber);

    Optional<User> findByEmail(String email);

    User findByAccountNumber(String accountNumber);

}
