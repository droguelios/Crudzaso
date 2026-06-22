package com.example.crudzaso.repository;

import com.example.crudzaso.entity.Database;
import com.example.crudzaso.entity.Users;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface DataBaseService {
    List<Database> getDatabasesByUser(Users users);

    Database createDatabase(Database database , Users users);

    void changeStatus(Long DatabaseId, String action);

    void deleteDatabase(Long databaseid);



}
