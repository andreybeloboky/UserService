## UserService Task

The primary objective of this assignment is to build a robust backend Java web application for tracking work items and managing users with their payment cards. The service is built using **Java 25**, **Spring Boot 4.0**, **Spring Data JPA**, and **Maven**. It utilizes **Liquibase** for database migrations, **Redis** for caching, and is fully containerized using **Docker**.

---

### Technical Stack
* **Java 25** & **Maven**
* **Spring Boot** (Data JPA, Web, Validation)
* **Liquibase** (Database migrations)
* **PostgreSQL** (Primary database)
* **Redis** (Caching layer)
* **MapStruct** (DTO/Entity mapping)
* **Docker & Docker Compose** (Containerization)
* **GitHub Actions** (CI/CD Pipeline)

---

### Getting Started

You can run the application on your local machine or via Docker.

#### Step 1: Prerequisites
Ensure you have the following installed:
* JDK 25
* Maven 4.1+
* Docker & Docker Compose

#### Step 2: Build the Project
Open a terminal, navigate to the project root folder (where `pom.xml` is located), and build the project:
```bash
mvn clean package -Dmaven.skip.test=true
```
This produces the executable JAR artifact in the `target/` directory.

#### Step 3: Run the Application

#### Running via Docker Compose
This will automatically spin up PostgreSQL, Redis, and the Spring Boot application using the `docker` Spring profile.
```bash
docker-compose up --build
```

### Database Architecture & Schema

Database schema creation is fully automated using **Liquibase** changelogs.
* Performance is optimized using **database indexes** on frequently queried columns.
* **JPA Auditing** is enabled globally to automatically populate `createdAt` and `updatedAt` timestamps for all entities.

### Tables & Relations
1. **users**
    * Columns: `id`, `name`, `surname`, `birth_date`, `email`, `active`, `created_at`, `updated_at`.
2. **payment_cards**
    * Columns: `id`, `user_id` (FK), `number`, `holder`, `expiration_date`, `active`, `created_at`, `updated_at`.
    * *Relationship:* One User can have multiple cards (**One-to-Many**).

### Deployment & CI/CD Pipeline

### Profiles
* `local`: Optimized for running the application on a local development machine.
* `docker`: Configured for containerized environments within `docker-compose`.

### CI/CD Pipeline (GitHub Actions)
On every `git push` to the main branches, the automated workflow executes the following steps:
1. **Build:** Compiles code and verifies dependencies.
2. **Testing:** Runs unit and integration tests.
3. **Code Analysis:** Executes static code analysis via **SonarQube** to ensure code quality.
4. **Artifact Creation:** Builds the final Docker image and pushes it to the registry.
