# Spring Boot Kafka Microservices

A simple e-commerce backend project built with Spring Boot and Apache Kafka. The application is divided into multiple microservices that communicate with each other using Kafka events.

## Services

- **order-service** – Handles order creation and publishes order events.
- **check-inventory** – Checks product availability and publishes the final order update.
- **email-service** – Listens for order updates and sends email notifications.
- **stock-service** – Processes order updates and maintains stock and sales analytics.
- **base-domains** – Contains shared DTOs and event classes used by the services.

## Technologies Used

- Java
- Spring Boot
- Apache Kafka
- Zookeeper
- MySQL
- Redis
- Maven
- Docker Compose

## Requirements

- JDK compatible with the project configuration
- Docker Desktop / Docker Engine
- Docker Compose
- Maven (or the included Maven Wrapper)

## Setup and Installation

**1. Clone the repository**

```bash
git clone <your-repository-url>
cd SpringBoot-Kafka-Microservices
```

**2. Configure environment variables**

Create a `.env` file from the example:

```bash
cp .env.example .env
```

On Windows, copy `.env.example` and rename the copy to `.env`.

Update the `.env` file with your own database credentials and Gmail App Password.

**3. Start infrastructure services**

Run the following command from the project root:

```bash
docker compose up -d
```

This starts Kafka, Zookeeper, MySQL, and Redis.

**4. Build the shared module**

Navigate to `base-domains` and run:

```bash
./mvnw clean install
```

On Windows:

```bash
mvnw.cmd clean install
```

**5. Run the microservices**

Start each Spring Boot application:

- order-service
- check-inventory
- email-service
- stock-service

When running through IntelliJ, set the working directory of each service to the project root so it can load the root `.env` file.

## Ports

| Component | Port |
|---|---:|
| Zookeeper | 2181 |
| Kafka | 9092 |
| MySQL | 3307 |
| Redis | 6379 |
| stock-service | 8081 |
| email-service | 8082 |
| check-inventory | 8084 |

The order-service uses the Spring Boot default port unless configured otherwise.

## Notes

- Start Docker infrastructure before running the Spring Boot services.
- The Kafka advertised listener is configured for applications running on the host machine.
- Never commit your actual `.env` file or expose passwords and credentials.
- This project is intended for learning.