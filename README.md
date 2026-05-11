# Bookstore Microservices

A comprehensive microservices-based e-commerce platform for a bookstore, built with Spring Boot, Spring Cloud, and modern technologies.

## Architecture Overview

This project implements a microservices architecture for a bookstore application, featuring:

- **Service Discovery**: Netflix Eureka for dynamic service registration and discovery
- **Configuration Management**: Spring Cloud Config for centralized configuration
- **API Gateway**: Spring Cloud Gateway for routing and cross-cutting concerns
- **Backend for Frontend (BFF)**: Specialized service for frontend interactions
- **Business Services**: Modular services handling specific domains (books, orders, payments, etc.)
- **Common Libraries**: Shared utilities for messaging, security, and common functionality
- **Distributed Tracing**: Micrometer with Brave for observability
- **Asynchronous Communication**: RabbitMQ for event-driven architecture
- **Data Persistence**: MongoDB for document storage, Redis for caching

## Services

- **discovery-server**: Service registry using Eureka
- **config-server**: Centralized configuration server
- **api-gateway**: API gateway for routing and security
- **bff**: Backend for Frontend service
- **book-service**: Book catalog management
- **cart-service**: Shopping cart functionality
- **identity-service**: User authentication and authorization
- **inventory-service**: Stock management
- **notification-service**: Email/SMS notifications
- **order-service**: Order processing
- **payment-service**: Payment processing
- **profile-service**: User profile management
- **promotion-service**: Discounts and promotions
- **search-service**: Search functionality powered by Elasticsearch
- **common-library**: Shared utilities and gRPC definitions
- **common-messaging**: Messaging utilities
- **common-security**: Security-related shared code

## Technology Stack

- **Framework**: Spring Boot 3.4.0
- **Cloud**: Spring Cloud 2024.0.0
- **Language**: Java 21
- **Build Tool**: Maven
- **Databases**: MongoDB, Elasticsearch, Redis
- **Messaging**: RabbitMQ (AMQP)
- **RPC**: gRPC with Protocol Buffers
- **Mapping**: MapStruct
- **Utilities**: Lombok, UUID Creator, DataFaker
- **Tracing**: Micrometer Tracing Bridge Brave
- **Testing**: Spring Boot Test

## Design Patterns & Best Practices

### Security & Authentication
- **Internal Trust Model**: Services trust user context propagated via HTTP headers from the API Gateway, enabling seamless distributed authentication without repeated verification
- **Distributed Security Context**: Security information flows seamlessly across service boundaries

### Messaging & Event-Driven Architecture
- **Envelope Pattern**: Standardized message wrappers with metadata (message ID, correlation ID, message type, timestamp, source) for reliable inter-service communication
- **Outbox Pattern**: Transactional event publishing ensures consistency and prevents message loss in distributed transactions
- **Strategy Pattern**: Flexible event processing with multiple handler implementations based on event types

### Data Management
- **Saga Pattern**: Correlation IDs track distributed transactions across services (e.g., order creation → inventory reservation → payment processing)
- **CQRS**: Separate read/write models with Elasticsearch for search functionality and MongoDB for transactional data

### Performance & Scalability
- **Batch Processing**: Bulk operations for efficient data updates and reduced I/O overhead
- **Caching**: Redis integration for performance optimization and reduced database load
- **gRPC**: High-performance RPC for synchronous inter-service calls

## Key Features

- **Microservices Architecture**: Loosely coupled, independently deployable services
- **Service Discovery**: Automatic registration and discovery of services
- **Centralized Configuration**: Externalized configuration management
- **API Gateway**: Single entry point with routing, filtering, and security
- **Event-Driven Communication**: Asynchronous messaging with RabbitMQ
- **gRPC Integration**: High-performance RPC for inter-service communication
- **Distributed Tracing**: End-to-end request tracing for observability
- **Caching**: Redis-based caching for improved performance
- **Security**: Shared security utilities across services
- **Data Mapping**: Automated DTO mapping with MapStruct
- **Reliable Messaging**: Outbox pattern ensures message delivery guarantees
- **Flexible Event Processing**: Strategy pattern for extensible event handling

## Prerequisites

Before running the application, ensure you have the following installed:

- **Java 21**: Required for building and running the services
- **Maven 3.6+**: For dependency management and building
- **Docker & Docker Compose**: For containerized deployment
- **External Services** (if not using full infrastructure):
  - MongoDB (for data persistence)
  - Redis (for caching)
  - RabbitMQ (for messaging)
  - Elasticsearch (for search functionality)

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/your-username/bookstore-microservices-demo.git
cd bookstore-microservices-demo
```

### Configuration

The application uses Spring Cloud Config for centralized configuration. Configuration files are located in the `config-server` service.

For local development, you may need to set up environment-specific configurations.

### Running the Application

#### Option 1: Using Docker Compose (Recommended for Internal Services)

This setup runs only the microservices in containers. Ensure external dependencies (MongoDB, Redis, RabbitMQ, Elasticsearch) are running separately.

1. Build the services:
   ```bash
   mvn clean package -DskipTests
   ```

2. Start the services with Docker Compose:
   ```bash
   docker-compose up --build
   ```

   This will start all microservices in the correct order based on dependencies.

3. Access the services:
   - API Gateway: http://localhost:9000
   - Eureka Discovery Server: http://localhost:8761
   - Config Server: http://localhost:9999
   - BFF Service: http://localhost:9001
   - And other services on their respective ports (9002-9009)

#### Option 2: Running Locally with Maven

For development purposes, you can run services individually:

1. Start external dependencies (MongoDB, Redis, etc.) locally or via Docker.

2. Start the Config Server:
   ```bash
   cd config-server
   mvn spring-boot:run
   ```

3. Start the Discovery Server:
   ```bash
   cd discovery-server
   mvn spring-boot:run
   ```

4. Start other services in any order:
   ```bash
   cd <service-name>
   mvn spring-boot:run
   ```

### Full Infrastructure Setup

For a complete setup including databases and messaging, you would need to extend the docker-compose.yml to include:

- MongoDB
- Redis
- RabbitMQ
- Elasticsearch

Example additional services:

```yaml
mongodb:
  image: mongo:7.0
  ports:
    - "27017:27017"
  networks:
    - bookteria-network

redis:
  image: redis:7-alpine
  ports:
    - "6379:6379"
  networks:
    - bookteria-network

rabbitmq:
  image: rabbitmq:3-management-alpine
  ports:
    - "5672:5672"
    - "15672:15672"
  networks:
    - bookteria-network

elasticsearch:
  image: elasticsearch:8.11.0
  environment:
    - discovery.type=single-node
    - xpack.security.enabled=false
  ports:
    - "9200:9200"
  networks:
    - bookteria-network
```

## API Documentation

Once the services are running, you can access API documentation via Swagger UI:

- API Gateway: http://localhost:9000/swagger-ui.html
- Individual services: http://localhost:<port>/swagger-ui.html

## Monitoring and Observability

- **Distributed Tracing**: Integrated with Micrometer and Brave
- **Health Checks**: Available at `/actuator/health` for each service
- **Metrics**: Exposed via `/actuator/metrics`
- **Eureka Dashboard**: Service discovery dashboard at http://localhost:8761

## Testing

Run tests for all services:

```bash
mvn test
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
