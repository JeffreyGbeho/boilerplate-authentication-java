package com.selfengineerjourney.auth.repository;

import com.selfengineerjourney.auth.entity.Role;
import com.selfengineerjourney.auth.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}
