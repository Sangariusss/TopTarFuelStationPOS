package ua.toptar.toptarfuelstationpos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.toptar.toptarfuelstationpos.model.FuelType;

/**
 * Repository interface for managing {@code FuelType} entities.
 * Provides CRUD operations and additional query methods for fuel types.
 */
public interface FuelTypeRepository extends JpaRepository<FuelType, Long> {}