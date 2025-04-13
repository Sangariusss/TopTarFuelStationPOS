package ua.toptar.toptarfuelstationpos.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a transaction in the system.
 * Stores details about a fuel purchase, including the fuel type, volume, total amount, and associated user.
 */
@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fuel_type_id", nullable = false)
    private FuelType fuelType;

    @Column(name = "fuel_type_name", nullable = false)
    private String fuelTypeName;

    @Column(name = "price_per_liter", nullable = false)
    private BigDecimal pricePerLiter;

    @Column(nullable = false)
    private BigDecimal volume;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Default constructor for JPA.
     */
    public Transaction() {}

    /**
     * Constructs a new {@code Transaction} with the specified fuel type and volume.
     * Automatically sets the fuel type name, price per liter, total amount, and transaction date.
     *
     * @param fuelType the fuel type associated with the transaction
     * @param volume the volume of fuel purchased
     */
    public Transaction(FuelType fuelType, BigDecimal volume) {
        this.fuelType = fuelType;
        this.fuelTypeName = fuelType.getName();
        this.pricePerLiter = fuelType.getPricePerLiter();
        this.volume = volume;
        this.totalAmount = volume.multiply(fuelType.getPricePerLiter());
        this.transactionDate = LocalDateTime.now();
    }
}