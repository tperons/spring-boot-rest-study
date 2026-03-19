package com.tperons.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tperons.entity.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByDescription(String description);

}
