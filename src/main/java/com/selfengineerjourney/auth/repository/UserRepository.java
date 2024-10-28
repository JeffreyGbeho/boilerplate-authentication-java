package com.selfengineerjourney.auth.repository;

import com.selfengineerjourney.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.email = :username OR u.username = :username")
    Optional<User> findByEmailOrUsername(@Param("username") String username);

    Optional<User> findByEmail(String email);
}
