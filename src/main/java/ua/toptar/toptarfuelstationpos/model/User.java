package ua.toptar.toptarfuelstationpos.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity representing a user in the system.
 * Stores user details such as username, password, and role.
 */
@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;
}