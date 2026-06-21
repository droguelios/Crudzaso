package com.example.crudzaso.entity;

import com.example.crudzaso.entity.enums.MotorDB;
import com.example.crudzaso.entity.enums.StatusDB;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "databases")
@AllArgsConstructor
@NoArgsConstructor
public class Database {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "motor_db", nullable = false)
    private MotorDB motorDB;
    @Column(name = "port", nullable = false , unique = true)
    private Integer port;
    @Column (name = "db_user", nullable = false)
    private String dbuser;
    @Column (name = "db_password", nullable = false)
    private String dbpassword;
    @Enumerated(EnumType.STRING)
    @Column (name = "status", nullable = false)
    private StatusDB status;
    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private Users users;



}
