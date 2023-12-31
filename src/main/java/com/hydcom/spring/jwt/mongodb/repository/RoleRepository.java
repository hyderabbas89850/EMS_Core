package com.hydcom.spring.jwt.mongodb.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hydcom.spring.jwt.mongodb.models.ERole;
import com.hydcom.spring.jwt.mongodb.models.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);
}
