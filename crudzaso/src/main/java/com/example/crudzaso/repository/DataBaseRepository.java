package com.example.crudzaso.repository;

import com.example.crudzaso.entity.Database;
import com.example.crudzaso.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataBaseRepository extends JpaRepository<Database, Long> {

    List<Database> findByUsers(Users users);

    long countByUsers(Users users);

    boolean existByPort(Integer port);
}
