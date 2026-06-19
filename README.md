# 🛒 eCommerce Backend

A scalable and modular eCommerce backend application built with **Spring Boot**, designed to support complete online shopping workflows including authentication, product management, shopping cart operations, order processing, checkout, and payment integration.

The application follows RESTful API principles and a layered architecture to ensure maintainability, scalability, and clean separation of concerns.

---

## 🚀 Features

### 🔐 Authentication & Authorization
- User registration and login
- JWT-based authentication
- Role-based access control
- Secure API endpoints

### 👤 User Management
- User profile management
- Customer account operations
- Role management

### 📦 Product Management
- Create, update, delete, and retrieve products
- Product categorization
- Product inventory management
- Product image support

### 🛒 Cart & Checkout
- Add products to cart
- Update cart items
- Remove products from cart
- Checkout workflow

### 📋 Order Management
- Place orders
- Track order status
- Retrieve order history
- Manage order lifecycle

### 💳 Payment Processing
- Payment initiation
- Payment status tracking
- Secure payment workflow integration

### ⚙️ Additional Features
- DTO-based request and response handling
- Global exception handling
- Data initialization and seeding
- Layered architecture
- RESTful API design

---

## 🛠️ Tech Stack

| Technology | Purpose |
|------------|----------|
| Java 21 | Programming Language |
| Spring Boot 3 | Backend Framework |
| Spring Data JPA | Database Access Layer |
| Spring Security | Authentication & Authorization |
| PostgreSQL | Relational Database |
| Hibernate | ORM Framework |
| Lombok | Boilerplate Code Reduction |
| ModelMapper | Object Mapping |
| Maven | Dependency Management |
| JWT | Secure Authentication |

---

## 📋 Prerequisites

Before running the project, ensure the following are installed:

- Java 21+
- PostgreSQL
- Maven 3.9+
- Git
- IntelliJ IDEA (Recommended)

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/<your-username>/eCommerce.git
cd eCommerce
```

### 2. Create PostgreSQL Database

```sql
CREATE DATABASE ecommerce_db;
```

### 3. Configure Application Properties

Update the database credentials in:

```properties
src/main/resources/application.properties
```

Example configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### 4. Build the Project

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

The application will start on:

```text
http://localhost:8080
```

---

## 📁 Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/tuhinK/eCommerce/
│   │       ├── auth/              # Authentication & Authorization
│   │       ├── user/              # User Management
│   │       ├── product/           # Product Catalog Management
│   │       ├── cartandcheckout/   # Cart & Checkout Workflow
│   │       ├── order/             # Order Management
│   │       ├── payment/           # Payment Processing
│   │       ├── data/              # Data Initialization & Seed Data
│   │       ├── commons/           # Shared Utilities & Constants
│   │       ├── config/            # Application Configuration
│   │       └── ECommerceApplication
│   │
│   └── resources/
│       └── application.properties
│
└── test/
```

---

## 🏗️ Architecture

The application follows a layered architecture:

```text
Controller Layer
       │
       ▼
Service Layer
       │
       ▼
Repository Layer
       │
       ▼
PostgreSQL Database
```

### Layer Responsibilities

- **Controllers** → Handle HTTP requests and responses
- **Services** → Implement business logic
- **Repositories** → Perform database operations
- **Entities/Models** → Represent database tables
- **DTOs** → Transfer data between layers

---

## 🔧 Configuration

The application can be configured through the `application.properties` file.

Key configurations include:

- Database connection settings
- JWT authentication settings
- JPA and Hibernate configurations
- Logging configurations
- Server port settings
- API base URL configuration

---

## 📚 API Modules

The backend exposes REST APIs for:

- Authentication
- Users
- Products
- Cart Management
- Checkout
- Orders
- Payments

Base API URL:

```text
/api/v1
```

---

## 🧪 Running Tests

Run all tests using:

```bash
mvn test
```

---

## 🔮 Future Enhancements

- OpenAPI / Swagger Documentation
- Flyway Database Migrations
- Redis Caching
- Elasticsearch Product Search
- Docker Containerization
- Kubernetes Deployment
- Email Notifications
- Event-Driven Architecture with Kafka
- Microservices Migration

---

## 👨‍💻 Author

**Tuhin Kumar Nandi**

Software Engineer (Backend) | Java | Spring | SQL

---