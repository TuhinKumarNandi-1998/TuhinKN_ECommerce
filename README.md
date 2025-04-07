# eCommerce Backend

A robust eCommerce platform backend built with Spring Boot, providing a complete set of APIs for managing products, categories, and user interactions.

## 🚀 Features

- Product Management (CRUD operations)
- Category Management
- Image Handling
- RESTful API endpoints
- JPA-based data persistence
- MySQL Database integration

## 🛠️ Tech Stack

- **Java 21**
- **Spring Boot 3.4.4**
- **Spring Data JPA**
- **MySQL Database**
- **Lombok** - For reducing boilerplate code
- **ModelMapper** - For object mapping
- **Maven** - For dependency management

## 📋 Prerequisites

- Java 21 or higher
- MySQL Server
- Maven
- Your favorite IDE (IntelliJ IDEA recommended)

## 🚀 Getting Started

1. **Clone the repository**
   ```bash
   git clone [your-repository-url]
   cd eCommerce
   ```

2. **Configure Database**
   - Create a MySQL database
   - Update the database configuration in `application.properties`

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/tuhinK/eCommerce/
│   │       ├── product/         # Product related components
│   │       │   ├── controllers/ # REST controllers
│   │       │   ├── dtos/       # Data Transfer Objects
│   │       │   ├── models/     # Entity models
│   │       │   └── services/   # Business logic
│   │       └── config/         # Configuration classes
│   └── resources/
│       └── application.properties
```

## 🔧 Configuration

The application can be configured through `application.properties`. Key configurations include:

- Database connection
- Server port
- JPA settings
- Logging levels
