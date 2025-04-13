package ua.toptar.toptarfuelstationpos.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.toptar.toptarfuelstationpos.model.User;
import ua.toptar.toptarfuelstationpos.repository.UserRepository;

/**
 * Service class for managing user-related operations.
 * Handles user registration with password encoding and role validation.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a new {@code UserService} with the specified dependencies.
     *
     * @param userRepository the repository for accessing user data
     * @param passwordEncoder the encoder for hashing passwords
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user with the specified username, password, and role.
     * Encodes the password and validates the input data.
     *
     * @param username the username for the new user
     * @param password the password for the new user
     * @param role the role for the new user (must be "USER" or "ADMIN")
     * @throws IllegalArgumentException if the username, password, or role is invalid, or if the username already exists
     */
    public void registerUser(String username, String password, String role) {
        // Перевірка на null або порожні значення
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (role == null || (!role.equals("USER") && !role.equals("ADMIN"))) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }

        // Перевірка на існування користувача
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        // Перевірка на заборонене ім'я "guest"
        if (username.equals("guest")) {
            throw new IllegalArgumentException("Cannot register user with username 'guest'");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        userRepository.save(user);
    }
}