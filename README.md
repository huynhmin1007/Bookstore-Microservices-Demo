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

