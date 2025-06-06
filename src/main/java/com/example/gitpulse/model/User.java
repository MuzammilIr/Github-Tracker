package com.example.gitpulse.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;
    private String username;
    private String avatarUrl;
    private String email;
    private String role;
}
