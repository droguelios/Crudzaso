package com.example.crudzaso.repository;

import com.example.crudzaso.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    Boolean existsByEmail(String email);
    long countByStatus(String status);
    List<Users> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);
}
