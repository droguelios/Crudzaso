package com.example.crudzaso.service;

import com.example.crudzaso.entity.Database;
import com.example.crudzaso.entity.Users;

import java.util.List;

public interface DataBaseService {
    List<Database> getDatabasesByUser(Users users);
    List<Database> getAllDatabases();
    List<Database> searchDatabases(String query);
    Database createDatabase(Database database, Users users);
    Database updateDatabase(Long id, Database database, Users users);
    void changeStatus(Long databaseId, String action);
    void deleteDatabase(Long databaseId);
    Database getDatabaseById(Long id);
}
