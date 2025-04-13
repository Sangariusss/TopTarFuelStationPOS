package ua.toptar.toptarfuelstationpos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the TopTar Fuel Station POS system.
 * Configures and starts the Spring Boot application.
 */
@SpringBootApplication
public class TopTarFuelStationPosApplication {

    /**
     * Entry point for the TopTar Fuel Station POS application.
     * Initializes and runs the Spring Boot application with the provided arguments.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(TopTarFuelStationPosApplication.class, args);
    }
}