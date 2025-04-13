package ua.toptar.toptarfuelstationpos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.toptar.toptarfuelstationpos.model.User;
import ua.toptar.toptarfuelstationpos.repository.UserRepository;
import ua.toptar.toptarfuelstationpos.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@code UserService} class.
 * Tests user registration functionality with various scenarios.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User savedUser;
    private User existingUser;

    /**
     * Sets up test data before each test.
     * Initializes users for testing.
     */
    @BeforeEach
    void setUp() {
        // Ініціалізація тестових даних
        savedUser = new User();
        savedUser.setId(1L);

        existingUser = new User();
        existingUser.setUsername("testUser");
        existingUser.setPassword("encoded_password123");
        existingUser.setRole("USER");
    }

    /**
     * Tests user registration with the "ROLE_USER" role.
     * Verifies that the user is saved with the correct details.
     */
    @Test
    void testRegisterUserWithRoleUser() {
        String username = "testUser";
        String password = "password123";
        String role = "USER";

        savedUser.setUsername(username);
        savedUser.setPassword("encoded_password123");
        savedUser.setRole(role);

        when(passwordEncoder.encode(password)).thenReturn("encoded_password123");
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        userService.registerUser(username, password, role);

        verify(userRepository).save(any(User.class));
        assertEquals("encoded_password123", savedUser.getPassword());
        assertEquals(username, savedUser.getUsername());
        assertEquals(role, savedUser.getRole());
    }

    /**
     * Tests user registration with the "ROLE_ADMIN" role.
     * Verifies that the user is saved with the correct details.
     */
    @Test
    void testRegisterUserWithRoleAdmin() {
        String username = "adminUser";
        String password = "admin123";
        String role = "ADMIN";

        savedUser.setUsername(username);
        savedUser.setPassword("encoded_admin123");
        savedUser.setRole(role);

        when(passwordEncoder.encode(password)).thenReturn("encoded_admin123");
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        userService.registerUser(username, password, role);

        verify(userRepository).save(any(User.class));
        assertEquals("encoded_admin123", savedUser.getPassword());
        assertEquals(username, savedUser.getUsername());
        assertEquals(role, savedUser.getRole());
    }

    /**
     * Tests user registration with an existing username.
     * Verifies that an {@code IllegalArgumentException} is thrown with the correct message.
     */
    @Test
    void testRegisterUserWithExistingUsername() {
        String username = "testUser";
        String password = "password123";
        String role = "USER";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser(username, password, role));
        assertEquals("Username already exists: " + username, exception.getMessage());
    }

    /**
     * Tests user registration with the reserved username "guest".
     * Verifies that an {@code IllegalArgumentException} is thrown with the correct message.
     */
    @Test
    void testRegisterUserWithGuestUsername() {
        String username = "guest";
        String password = "password123";
        String role = "USER";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser(username, password, role));
        assertEquals("Cannot register user with username 'guest'", exception.getMessage());
    }

    /**
     * Tests user registration with a null username.
     * Verifies that an {@code IllegalArgumentException} is thrown with the correct message.
     */
    @Test
    void testRegisterUserWithNullUsername() {
        String username = null;
        String password = "password123";
        String role = "USER";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser(username, password, role));
        assertEquals("Username cannot be null or empty", exception.getMessage());
    }

    /**
     * Tests user registration with an empty username.
     * Verifies that an {@code IllegalArgumentException} is thrown with the correct message.
     */
    @Test
    void testRegisterUserWithEmptyUsername() {
        String username = "";
        String password = "password123";
        String role = "USER";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser(username, password, role));
        assertEquals("Username cannot be null or empty", exception.getMessage());
    }

    /**
     * Tests user registration with a null password.
     * Verifies that an {@code IllegalArgumentException} is thrown with the correct message.
     */
    @Test
    void testRegisterUserWithNullPassword() {
        String username = "testUser";
        String password = null;
        String role = "USER";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser(username, password, role));
        assertEquals("Password cannot be null or empty", exception.getMessage());
    }

    /**
     * Tests user registration with an empty password.
     * Verifies that an {@code IllegalArgumentException} is thrown with the correct message.
     */
    @Test
    void testRegisterUserWithEmptyPassword() {
        String username = "testUser";
        String password = "";
        String role = "USER";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser(username, password, role));
        assertEquals("Password cannot be null or empty", exception.getMessage());
    }

    /**
     * Tests user registration with a null role.
     * Verifies that an {@code IllegalArgumentException} is thrown with the correct message.
     */
    @Test
    void testRegisterUserWithNullRole() {
        String username = "testUser";
        String password = "password123";
        String role = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser(username, password, role));
        assertEquals("Invalid role: null", exception.getMessage());
    }

    /**
     * Tests user registration with an invalid role.
     * Verifies that an {@code IllegalArgumentException} is thrown with the correct message.
     */
    @Test
    void testRegisterUserWithInvalidRole() {
        String username = "testUser";
        String password = "password123";
        String role = "INVALID_ROLE";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser(username, password, role));
        assertEquals("Invalid role: " + role, exception.getMessage());
    }
}