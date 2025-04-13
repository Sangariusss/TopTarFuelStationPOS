package ua.toptar.toptarfuelstationpos.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Entity representing a fuel type in the system.
 * Stores information about the fuel type, such as its name and price per liter.
 */
@Entity
@Table(name = "fuel_types")
@Data
public class FuelType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // "A95", "ДП"

    @Column(nullable = false)
    private BigDecimal pricePerLiter;

    /**
     * Default constructor for JPA.
     */
    public FuelType() {
    }

    /**
     * Constructs a new {@code FuelType} with the specified name and price per liter.
     *
     * @param name the name of the fuel type (e.g., "A95", "ДП")
     * @param pricePerLiter the price per liter of the fuel type
     */
    public FuelType(String name, BigDecimal pricePerLiter) {
        this.name = name;
        this.pricePerLiter = pricePerLiter;
    }
}