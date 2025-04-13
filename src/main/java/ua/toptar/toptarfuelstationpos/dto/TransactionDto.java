package ua.toptar.toptarfuelstationpos.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for representing transaction data.
 * Used to transfer transaction details between layers of the application.
 */
@Data
public class TransactionDto {

    private Long id;
    private Long fuelTypeId;
    private String fuelTypeName;
    private BigDecimal pricePerLiter;
    private BigDecimal volume;
    private BigDecimal totalAmount;
    private LocalDateTime transactionDate;
    private String username;
}