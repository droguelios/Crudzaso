package com.example.crudzaso.service;

import com.example.crudzaso.entity.Database;
import com.example.crudzaso.entity.Users;
import com.example.crudzaso.entity.enums.Rol;
import com.example.crudzaso.entity.enums.StatusDB;
import com.example.crudzaso.entity.enums.MotorDB;
import com.example.crudzaso.repository.DataBaseRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DataServiceImpl implements DataBaseService {

    private final DataBaseRepository dataBaseRepository;
    private final EmailService emailService;

    @Override
    public List<Database> getDatabasesByUser(Users users) {
        return dataBaseRepository.findByUsers(users);
    }

    @Override
    public List<Database> getAllDatabases() {
        return dataBaseRepository.findAll();
    }

    @Override
    public List<Database> searchDatabases(String query) {
        return dataBaseRepository.findByNameContainingIgnoreCaseOrUsers_NameContainingIgnoreCase(query, query);
    }

    @Override
    public Database getDatabaseById(Long id) {
        return dataBaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Database not found"));
    }

    @Override
    public Database createDatabase(Database database, Users users) {
        if (users.getRol().name().equals("USER") && dataBaseRepository.countByUsers(users) >= 3){
            throw new RuntimeException("limit reached: max 3 databases allowed");
        }
        if (dataBaseRepository.existsByPort(database.getPort())){
            throw new RuntimeException("the port " + database.getPort() + " is already in use");
        }

        String dbUser = "user_" + System.currentTimeMillis();
        String dbPass= UUID.randomUUID().toString().substring(0,12);

        database.setUserconection(dbUser);
        database.setPasswordconection(dbPass);

        runConteinerwithcredentials(database);

        database.setUsers(users);
        database.setStatus(StatusDB.RUNNING);
        Database saveDb = dataBaseRepository.save(database);

        emailService.sendEmail(
            users.getEmail(),
            "New database created",
            "Hello " + users.getName() + ",\n\n" +
            "Your database " + saveDb.getName() + " has been created successfully.\n\n" +
            "Engine: " + database.getEngine() + "\n" +
            "Port: " + database.getPort() + "\n" +
            "Connection user: " + dbUser + "\n" +
            "Password: " + dbPass
        );
        return saveDb;
    }

    @Override
    public Database updateDatabase(Long id, Database database, Users users) {
        Database existing = getDatabaseById(id);

        if (existing.getStatus() == StatusDB.ARCHIVED) {
            throw new RuntimeException("Cannot edit an archived database");
        }
        if (!existing.getUsers().getId().equals(users.getId()) && users.getRol() != Rol.ADMIN) {
            throw new RuntimeException("You are not the owner of this database");
        }
        if (database.getEngine() != null && !existing.getEngine().equals(database.getEngine())) {
            throw new RuntimeException("Engine cannot be changed after creation");
        }

        existing.setName(database.getName());
        existing.setDescription(database.getDescription());
        existing.setUpdateDate(LocalDateTime.now());

        return dataBaseRepository.save(existing);
    }

    @Override
    public void changeStatus(Long databaseId, String action) {
        Database database = getDatabaseById(databaseId);

        if (database.getStatus() == StatusDB.ARCHIVED) {
            throw new RuntimeException("Cannot change status of an archived database");
        }

        switch (action.toUpperCase()) {
            case "START" -> database.setStatus(StatusDB.RUNNING);
            case "STOP" -> database.setStatus(StatusDB.STOPPED);
            case "ARCHIVE" -> database.setStatus(StatusDB.ARCHIVED);
            default -> throw new RuntimeException("Invalid action: " + action);
        }

        database.setUpdateDate(LocalDateTime.now());
        dataBaseRepository.save(database);
    }

    @Override
    public void deleteDatabase(Long databaseId) {
        Database database = getDatabaseById(databaseId);
        dataBaseRepository.delete(database);
    }

    private void runConteinerwithcredentials(Database database) {
        String containerName = "crudzaso_" + database.getEngine().name().toLowerCase() + "_" + database.getPort();
        String image = switch (database.getEngine()) {
            case MYSQL -> "mysql:8.0";
            case POSTGRES -> "postgres:16";
            case MONGODB -> "mongo:7";
        };
        String envVars = switch (database.getEngine()) {
            case MYSQL -> String.format("-e MYSQL_ROOT_PASSWORD=%s -e MYSQL_DATABASE=%s", database.getPasswordconection(), database.getName());
            case POSTGRES -> String.format("-e POSTGRES_PASSWORD=%s -e POSTGRES_DB=%s", database.getPasswordconection(), database.getName());
            case MONGODB -> String.format("-e MONGO_INITDB_ROOT_PASSWORD=%s", database.getPasswordconection());
        };
        int defaultPort = switch (database.getEngine()) {
            case MYSQL -> 3306;
            case POSTGRES -> 5432;
            case MONGODB -> 27017;
        };

        try {
            List<String> command = new ArrayList<>();
            command.add("docker");
            command.add("run");
            command.add("-d");
            command.add("--name");
            command.add(containerName);
            command.add("-p");
            command.add(database.getPort() + ":" + defaultPort);
            command.addAll(Arrays.asList(envVars.split(" ")));
            command.add(image);

            ProcessBuilder pb = new ProcessBuilder(command).inheritIO();
            Process process = pb.start();
            int exit = process.waitFor();
            if (exit != 0) throw new RuntimeException("Docker container failed to start");
        } catch (Exception e) {
            throw new RuntimeException("Error starting container: " + e.getMessage());
        }
    }

}
