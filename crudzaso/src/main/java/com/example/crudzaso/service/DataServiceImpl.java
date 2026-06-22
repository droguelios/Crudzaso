package com.example.crudzaso.service;

import com.example.crudzaso.entity.Database;
import com.example.crudzaso.entity.Users;
import com.example.crudzaso.entity.enums.StatusDB;
import com.example.crudzaso.repository.DataBaseRepository;
import com.example.crudzaso.repository.DataBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DataServiceImpl implements DataBaseService {

    private final DataBaseRepository dataBaseRepository;

    @Override
    public List<Database> getDatabasesByUser(Users users) {
        return dataBaseRepository.findByUsers(users);
    }

    @Override
    public Database createDatabase(Database database, Users users) {
        if (users.getRol().name().equals("USER") && dataBaseRepository.countByUsers(users) >= 3) {
            throw new RuntimeException("Límite alcanzado: Máximo 3 bases de datos permitidas.");
        }
        if (dataBaseRepository.existByPort(database.getPort())) {
            throw new RuntimeException("the port" + database.getPort() + "it's already use");
        }
        database.setUsers(users);
        database.setStatus(StatusDB.CREATED);
        return dataBaseRepository.save(database);
    }
    @Override
    public void changeStatus(Long databaseId, String action) {
    }
    @Override
    public void deleteDatabase(Long databaseId) {
    }

}
