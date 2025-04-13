package ua.toptar.toptarfuelstationpos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.toptar.toptarfuelstationpos.model.User;

import java.util.Optional;

/**
 * Repository interface for managing {@code User} entities.
 * Provides CRUD operations and custom query methods for users.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return an {@code Optional} containing the user if found, or empty if not found
     */
    Optional<User> findByUsername(String username);
}