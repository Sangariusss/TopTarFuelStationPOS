# TopTar Fuel Station POS

## Зміст

- [Огляд](#огляд)
- [Технології](#технології)
- [Функціонал](#функціонал)
- [Структура проєкту](#структура-проєкту)
- [Налаштування](#налаштування)
- [Використання](#використання)
- [Внесок](#внесок)
- [Ліцензія](#ліцензія)

## Огляд

`TopTar Fuel Station POS` — це система для точки продажу (POS), розроблена для автозаправної станції. Вона дозволяє обробляти транзакції купівлі пального як для зареєстрованих, так і для гостей, відображати статистику в адмін-панелі та підтверджувати транзакції на окремій сторінці. У проєкті використовується **Spring Boot** для серверної логіки, **Thymeleaf** для шаблонізації та **JavaScript** для динамічного оновлення сторінок.

## Технології

- **Java 21** — основна мова програмування.
- **Spring Boot** — фреймворк для створення вебзастосунків.
- **Thymeleaf** — шаблонізатор HTML.
- **Tailwind CSS** — фреймворк для стилізації інтерфейсу.
- **Font Awesome** — набір іконок.
- **Maven** — система управління залежностями.
- **H2 Database** — вбудована БД для тестування.
- **MySQL** — основна БД для продакшену.
- **JavaScript** — динамічні оновлення сторінок без перезавантаження.

## Функціонал

- **Інтерфейс POS**: введення об’єму або суми пального, вибір типу пального, розрахунок вартості.
- **Гостьові транзакції**: підтримка створення транзакцій без входу в систему.
- **Підтвердження транзакції**: відображення сторінки успішного завершення.
- **Адмін-панель**:
    - **Дашборд**: статистика (кількість, дохід, середній чек).
    - **Транзакції**: перегляд транзакцій з пагінацією (оновлення без перезавантаження).
    - **Аналітика**: графіки продажів та доходів.
- **Кабінет користувача**:
    - Транзакції.
- **Реєстрація та авторизація**: створення облікових записів та доступ до захищених сторінок.
- **Валідація**: перевірка даних (наприклад, мінімальний об’єм — 1 л).
- **Логування**: журнал подій (створення транзакцій).

## Структура проєкту

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

## Налаштування

1. **Клонування репозиторію:**
   ```bash
   git clone https://github.com/Sangariusss/TopTarFuelStationPOS.git
   cd TopTarFuelStationPOS
   ```
   
2. **Налаштування бази даних:**
   - Переконайтесь, що MySQL встановлено та запущено.
   - Створіть базу даних:
     ```sql
     CREATE DATABASE toptar_fuel_db;
     ```
   - Основна конфігурація знаходиться у файлі `application.yml`, де використовуються змінні оточення з дефолтними значеннями:
     ```yaml
     spring:
       datasource:
         url: jdbc:mysql://localhost:3306/toptar_fuel_db?useSSL=false&serverTimezone=UTC
         username: ${DB_USERNAME:root}
         password: ${DB_PASSWORD:}
     ```
   - **Однак при активному профілі `local` (встановленому через `spring.profiles.active: local`) використовуються налаштування з `application-local.yml`, які перекривають ці значення.**  
     Якщо потрібно, змініть поля `username` і `password` відповідно до вашого облікового запису MySQL:
     ```yaml
     # application-local.yml
     spring:
       datasource:
         username: ваше_ім’я
         password: ваш_пароль
     ```
   - Такий підхід дозволяє розділити загальні та локальні налаштування і не зберігати чутливі дані в основному конфігураційному файлі.

3. **Збірка проєкту:**
   ```bash
   mvn clean install
   ```

4. **Запуск:**
   ```bash
   mvn spring-boot:run
   ```

5. **Доступ до інтерфейсу:**
    - `http://localhost:8080/pos` — POS інтерфейс
    - `http://localhost:8080/admin/dashboard` — адмінпанель
    - `http://localhost:8080/admin/transactions` — транзакції
    - `http://localhost:8080/admin/analytics` — аналітика
    - `http://localhost:8080/user/transactions` — транзакції користувача
    - `http://localhost:8080/login` — вхід
    - `http://localhost:8080/register` — реєстрація

## Використання

1. **POS Інтерфейс:**
    - Оберіть тип пального, введіть обсяг або суму, натисніть “Оплатити”.

2. **Транзакції для гостей:**
    - Якщо користувач неавторизований, транзакція все одно буде створена (записується як "гостьова").

3. **Адмін-панель:**
    - Після входу — перегляд статистики, списку транзакцій та аналітики.

4. **Кабінет користувача:**
    - Перегляд власних транзакцій (після входу).

5. **Управління користувачами:**
    - `/register` — реєстрація.
    - `/login` — вхід до системи.

## Внесок

Будь-яка допомога вітається! Щоб внести зміни:

1. Форкніть репозиторій.
2. Створіть нову гілку:
   ```bash
   git checkout -b feature/your-feature
   ```
3. Внесіть зміни, зробіть коміт:
   ```bash
   git commit -m "Add your feature"
   ```
4. Надішліть зміни:
   ```bash
   git push origin feature/your-feature
   ```
5. Створіть Pull Request у GitHub.

## Ліцензія

Цей проєкт поширюється під ліцензією MIT. Деталі — у файлі [LICENSE](LICENSE).