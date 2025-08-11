
# Spring Boot + ShedLock: Distributed Scheduled Jobs    

## Project Overview

This is a simple proof of concept demonstrating how to use **ShedLock** with **Spring Boot** to ensure that scheduled jobs running across multiple instances **do not execute concurrently**.  

Ideal for scenarios where you have multiple replicas of your application running (in containers, cloud clusters, etc) and need to guarantee that certain scheduled tasks run only once at a time.

---

## When to Use This

- Processing batch jobs or queues in distributed environments  
- Scheduled tasks that must be run exclusively by one instance at a time  
- Preventing race conditions or data inconsistency in critical routines  
- Use cases requiring distributed locking for scheduled jobs  

---

## About ShedLock

- Java library to manage distributed locks for scheduled jobs  
- Integrates easily with Spring Boot `@Scheduled`  
- Uses a database table (PostgreSQL, MySQL, MongoDB, etc) for locking  
- Configurable lock duration to avoid deadlocks  
- Lightweight and non-intrusive  

---

## Running Locally & Observing Behavior

### Quick Start

1. Start PostgreSQL database via Docker Compose:

```bash
docker compose up -d db
```

2. Export environment variables to connect app to local DB:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/shedlock_db
export SPRING_DATASOURCE_USERNAME=shedlock_user
export SPRING_DATASOURCE_PASSWORD=shedlock_pass
```

3. Run the Spring Boot app (once or multiple times to simulate distributed environment):

```bash
mvn spring-boot:run
```

---

### What to Watch in Logs

- The **order generation job** runs in **all** instances (no lock needed)  
- The **order processing job** runs in **only one instance at a time**, thanks to ShedLock  

---

## Docker Compose Setup

This configuration runs PostgreSQL and **two instances** of the app to demonstrate distributed locking.

```yaml
version: '3.8'
services:
  db:
    container_name: postgres
    image: postgres:15
    environment:
      POSTGRES_DB: shedlock_db
      POSTGRES_USER: shedlock_user
      POSTGRES_PASSWORD: shedlock_pass
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
      - ./db-init.sql:/docker-entrypoint-initdb.d/db-init.sql
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U shedlock_user -d shedlock_db"]
      interval: 5s
      timeout: 5s
      retries: 10

  app1:
    build: .
    depends_on:
      db:
        condition: service_healthy
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/shedlock_db
      SPRING_DATASOURCE_USERNAME: shedlock_user
      SPRING_DATASOURCE_PASSWORD: shedlock_pass
    ports:
      - "8081:8080"

  app2:
    build: .
    depends_on:
      db:
        condition: service_healthy
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/shedlock_db
      SPRING_DATASOURCE_USERNAME: shedlock_user
      SPRING_DATASOURCE_PASSWORD: shedlock_pass
    ports:
      - "8082:8080"

volumes:
  postgres-data:
```

---

## Dockerfile

Builds a lightweight container running the Spring Boot fat jar on Eclipse Temurin OpenJDK 21 Alpine:

```dockerfile
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/springboot-shedlock-cronjob-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## How to Use

1. Build the project jar:

```bash
mvn clean package
```

2. Build and start the environment (Postgres + 2 app instances):

```bash
docker compose up --build
```

3. Follow logs and observe ShedLock coordination:

```bash
docker compose logs -f app1 app2
```

4. Stop the environment:

```bash
docker compose down
```

Add `-v` to remove volumes and clean DB data if needed.

---

## Troubleshooting Tips

- Make sure ports 5432, 8081, 8082 are free or adjust Docker Compose accordingly.
- Check DB health with `docker compose logs db` if apps fail to connect.

---

## License

MIT License — free to use, modify, and share. Please keep attribution to the original repository.

---

This setup is a hands-on demonstration of distributed job locking in Spring Boot applications using ShedLock — perfect for workshops, proofs of concept, or as a starting point for production solutions.
