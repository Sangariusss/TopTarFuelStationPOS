# TopTar Fuel Station POS

## Table of Contents

- [Overview](#overview)
- [Technologies](#technologies)
- [Features](#features)
- [File Structure](#file-structure)
- [Setup](#setup)
- [Usage](#usage)
- [Contributions](#contributions)
- [License](#license)

## Overview

`TopTar Fuel Station POS` is a Point of Sale (POS) system designed for a fuel station. It enables processing fuel purchase transactions for both authenticated and guest users, displaying statistics in an admin panel, and confirming transactions with a success page. The project showcases the use of Spring Boot for server-side logic, Thymeleaf for template rendering, and JavaScript for dynamic page updates.

## Technologies

- **Java 21**: Core programming language for server-side logic.
- **Spring Boot**: Framework for building web applications with an embedded server.
- **Thymeleaf**: Templating engine for generating HTML pages.
- **Tailwind CSS**: CSS framework for styling the user interface.
- **Font Awesome**: Icons to enhance the UI.
- **Maven**: Dependency management and build tool.
- **H2 Database**: Embedded database for testing.
- **MySQL**: Primary database for production use.
- **JavaScript**: For dynamic page updates without reloading.

## Features

- **POS Interface**: Allows input of fuel volume or amount, selection of fuel type, and calculation of total cost.
- **Guest Transactions**: Supports transaction creation for unauthenticated users using a predefined guest account.
- **Transaction Confirmation**: Displays a confirmation page after successful transaction completion.
- **Admin Panel**:
  - **Dashboard**: Displays statistics (total transactions, revenue, average transaction).
  - **Transactions**: Lists all transactions with pagination, updated dynamically via JavaScript.
  - **Analytics**: Provides detailed analytics on sales and revenue.
- **User Panel**:
  - **Transactions**: Lists transactions for the authenticated user.
- **Registration**: Users can create accounts to access the system.
- **Authorization**: Secure login for registered users to access protected areas like the admin and user panels.
- **JavaScript**: Dynamically updates the transaction table without page reloads.
- **Logging**: Uses Java Logging to track operations (e.g., transaction creation).
- **Validation**: Ensures valid input (e.g., minimum volume of 1 liter).

## File Structure

```
src/main/java/ua/toptar/toptarfuelstationpos/
├── config/                                                  # Configuration classes
│   └── SecurityConfig.java                                  # Security configuration (e.g., Spring Security)
├── controller/                                              # Controllers for handling requests
│   ├── AdminController.java                                 # Admin panel logic
│   ├── HomeController.java                                  # Home page logic
│   ├── LoginController.java                                 # Login handling
│   ├── PosController.java                                   # POS interface logic
│   ├── RegisterController.java                              # User registration handling
│   ├── TransactionController.java                           # Transaction-related endpoints
│   └── UserController.java                                  # User panel logic
├── dto/                                                     # Data Transfer Objects (DTOs)
│   └── TransactionDto.java                                  # Transaction data transfer object
├── model/                                                   # Data models
│   ├── FuelType.java                                        # Fuel type entity
│   ├── Transaction.java                                     # Transaction entity
│   └── User.java                                            # User entity
├── repository/                                              # Repositories for database interaction
│   ├── FuelTypeRepository.java                              # Fuel type repository
│   ├── TransactionRepository.java                           # Transaction repository
│   └── UserRepository.java                                  # User repository
├── service/                                                 # Business logic services
│   ├── TransactionService.java                              # Transaction-related business logic
│   └── UserService.java                                     # User-related business logic
└── TopTarFuelStationPosApplication.java                     # Main application class
src/main/resources/
├── static/                                                  # Static resources (CSS, JS)
│   ├── css/
│   │   ├── all.min.css                                      # Font Awesome styles
│   │   ├── animate.min.css                                  # Animation styles
│   │   └── tailwind.min.css                                 # Tailwind CSS styles
│   ├── images/
│   │   └── toptar-logo.png                                  # Project logo
│   ├── js/
│   │   ├── admin-analytics.js                               # Admin analytics logic
│   │   ├── admin-dashboard.js                               # Admin dashboard logic
│   │   ├── admin-transactions.js                            # JavaScript for transaction pagination
│   │   ├── chart.min.js                                     # Chart.js library for analytics
│   │   ├── fragments.js                                     # Logic for reusable fragments
│   │   ├── index.js                                         # POS interface logic
│   │   ├── login.js                                         # Login page logic
│   │   ├── register.js                                      # Registration page logic
│   │   └── user-transactions.js                             # User transactions logic
│   └── webfonts/                                            # Font Awesome web fonts
├── templates/                                               # Thymeleaf templates
│   ├── admin-analytics.html                                 # Analytics page
│   ├── admin-dashboard.html                                 # Admin dashboard page
│   ├── admin-transactions.html                              # Admin transactions page
│   ├── fragments.html                                       # Reusable HTML fragments
│   ├── index.html                                           # Home page
│   ├── login.html                                           # Login page
│   ├── register.html                                        # Registration page
│   └── user-transactions.html                               # User transactions page
├── application.yml                                          # Main application configuration
├── application-local.yml                                    # Local environment configuration
└── application-test.yml                                     # Test environment configuration
src/test/java/ua/toptar/toptarfuelstationpos/
├── TransactionServiceTest.java                              # Unit tests for TransactionService
└── UserServiceTest.java                                     # Unit tests for UserService
.gitignore                                                   # Files ignored by Git
LICENSE                                                      # Project license file
pom.xml                                                      # Maven configuration file
README.md                                                    # Project overview and instructions
```

## Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Sangariusss/TopTarFuelStationPOS.git
   cd TopTarFuelStationPOS
   ```

2. **Configure the database:**
   - Make sure MySQL is installed and running.
   - Create a database named `toptar_fuel_db`:
     ```sql
     CREATE DATABASE toptar_fuel_db;
     ```
   - The main database configuration is located in `application.yml`, using environment variables with default values:
     ```yaml
     spring:
       datasource:
         url: jdbc:mysql://localhost:3306/toptar_fuel_db?useSSL=false&serverTimezone=UTC
         username: ${DB_USERNAME:root}
         password: ${DB_PASSWORD:}
     ```
   - However, when the `local` profile is active (`spring.profiles.active=local`), the settings from `application-local.yml` take precedence and override these values.
     For local development, you can define your MySQL credentials directly in that file:
     ```yaml
     # application-local.yml
     spring:
       datasource:
         username: your-username
         password: your-password
     ```
   - This approach keeps environment variables flexible for production while allowing easy customization for local development without modifying the main configuration file.

3. **Build the project:**
   ```bash
   mvn clean install
   ```

4. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application:**
   - Open your browser and go to:
      - `http://localhost:8080/pos` — POS interface
      - `http://localhost:8080/admin/dashboard` — Admin dashboard (login required)
      - `http://localhost:8080/admin/transactions` — Admin transactions (login required)
      - `http://localhost:8080/admin/analytics` — Admin analytics (login required)
      - `http://localhost:8080/user/transactions` — User transactions (login required)
      - `http://localhost:8080/login` — Login page
      - `http://localhost:8080/register` — Registration page

## Usage

1. **POS Interface:**
    - On the `/pos` page, select a fuel type, enter the volume (liters) or amount (UAH), and submit the form to create a transaction.
    - The system automatically calculates the total amount or volume based on the input.

2. **Guest Transactions:**
    - Unauthenticated users can create transactions, which are associated with a predefined `guest` user account.

3. **Admin Panel:**
    - **Dashboard** (`/admin/dashboard`): View overall statistics and recent transactions (after logging in).
    - **Transactions** (`/admin/transactions`): Browse all transactions with pagination (after logging in).
    - **Analytics** (`/admin/analytics`): View detailed sales and revenue analytics (after logging in).

4. **User Panel:**
    - **Transactions** (`/user/transactions`): Browse user-specific transactions (after logging in).

5. **User Management:**
    - **Register**: Visit `/register` to create a new account.
    - **Login**: Use `/login` to access protected areas like the admin and user panels.

## Contributions

Contributions are welcome! To add features, fix bugs, or improve the system:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature`).
3. Make your changes and commit them (`git commit -m "Add your feature"`).
4. Push to your fork (`git push origin feature/your-feature`).
5. Submit a Pull Request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.