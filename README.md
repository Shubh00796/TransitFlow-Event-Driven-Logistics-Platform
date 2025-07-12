TransitFlow ­– Event­Driven Logistics Platform

Table of Contents 1. Overview 2. Architecture 3. Features 4. Tech Stack 5. Prerequisites 6. Getting Started 7. Docker & Docker Compose 8. API Reference 9. Testing 10. CI/CD 11. Deployment 12. Monitoring & Logging 13. Contributing 14. License 15. Contact

⸻

Overview

TransitFlow is a modular, event-driven logistics platform built with Spring Boot and Kafka. It simulates a typical delivery pipeline: 1. Order Service accepts and validates customer orders. 2. Inventory Service reserves stock. 3. Dispatch Service arranges shipment and generates tracking. 4. Delivery Service updates delivery status and proof-of-delivery. 5. Analytics Service aggregates events for reporting and KPIs.

flowchart LR subgraph Event Bus K(Kafka Cluster) end

subgraph Services OS[Order Service] IS[Inventory Service] DS[Dispatch Service] DelS[Delivery Service] AS[Analytics Service] end

OS -- OrderCreated --> K K -- InventoryReserved --> IS IS -- ReservationConfirmed --> K K -- ShipmentDispatched --> DS DS -- DispatchConfirmed --> K K -- ShipmentDelivered --> DelS DelS -- DeliveryConfirmed --> K K -- * --> AS

OS -->|PostgreSQL| PG1[(Orders DB)] IS -->|PostgreSQL| PG2[(Inventory DB)] DS -->|PostgreSQL| PG3[(Dispatch DB)] DelS -->|PostgreSQL| PG4[(Delivery DB)] AS -->|MongoDB| MG[(Analytics DB)]

Features • Event-Driven: Loose coupling via Kafka topics • Microservices: Independently deployable Spring Boot modules • Persistence: Each service has its own PostgreSQL schema • Observability: Prometheus + Grafana metrics (see Monitoring & Logging) • Authentication: JWT-based auth on API gateway (if extended) • Resilience: Retry, dead-letter topics, circuit breakers (Hystrix/Resilience4j)

Tech Stack

Layer Technology Language Java 17 Framework Spring Boot (Spring Cloud) Messaging Apache Kafka Databases PostgreSQL, MongoDB Containerization Docker, Docker Compose CI/CD GitHub Actions Monitoring Prometheus, Grafana Testing JUnit, Mockito, Testcontainers Infra as Code (Optional) Terraform

Prerequisites • Java 17+ SDK • Docker & Docker Compose • (Local) Kafka & Zookeeper, or a running Kafka cluster • Maven 3.6+

Getting Started 1. Clone the repo

git clone https://github.com/Shubh00796/TransitFlow-Event-Driven-Logistics-Platform.git cd TransitFlow-Event-Driven-Logistics-Platform

mvn clean install

docker-compose up -d zookeeper kafka

From project root

./run-services.sh

Docker & Docker Compose

All services can be spun up via Docker Compose:

version: '3.8' services: zookeeper: image: bitnami/zookeeper:latest ... kafka: image: bitnami/kafka:latest ... orders: build: ./order-service ports: ["8081:8080"] depends_on: [kafka] inventory: build: ./inventory-service ports: ["8082:8080"] depends_on: [kafka]

... dispatch, delivery, analytics

http://localhost:8081/swagger-ui.html