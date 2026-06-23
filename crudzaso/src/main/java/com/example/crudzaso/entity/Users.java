package com.example.crudzaso.entity;

import com.example.crudzaso.entity.enums.Rol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Rol rol;

    private String status = "ACTIVE";
    private LocalDateTime registrationDate = LocalDateTime.now();
    private LocalDateTime updateDate = LocalDateTime.now();
}
