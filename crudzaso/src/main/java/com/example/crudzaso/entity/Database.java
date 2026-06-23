package com.example.crudzaso.entity;

import com.example.crudzaso.entity.enums.MotorDB;
import com.example.crudzaso.entity.enums.StatusDB;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "databases")
@AllArgsConstructor
@NoArgsConstructor
public class Database {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private MotorDB engine;
    private String userconection;
    private String passwordconection;

    @Column(unique = true)
    private Integer port;

    @Enumerated(EnumType.STRING)
    @Column (name = "status", nullable = false)
    private StatusDB status= StatusDB.CREATED;
    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime updateDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private Users users;



}
