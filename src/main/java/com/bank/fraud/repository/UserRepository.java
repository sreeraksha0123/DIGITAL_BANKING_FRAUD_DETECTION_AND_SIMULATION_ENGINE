package com.bank.fraud.repository;

import com.bank.fraud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    // ---------- AUTHENTICATION & LOGIN ----------

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    // ---------- USER MANAGEMENT ----------

    List<User> findByRole(String role);

    List<User> findByActiveTrue();

    List<User> findByActiveFalse();
}
