# 🚛 TransitFlow: Event-Driven Logistics Platform

**TransitFlow** is a modular, event-driven logistics platform designed for **scalable**, **resilient**, and **decoupled** order-to-delivery workflows. Each service owns its domain and communicates asynchronously using **Kafka**, enabling robust and fault-tolerant operations across the supply chain.

---

## 🧩 Core Modules

| Module                    | Responsibility                                   |
|---------------------------|--------------------------------------------------|
| `transitflow-common`      | Shared DTOs, events, and enums                   |
| `transitflow-order`       | Handles order creation and emits events          |
| `transitflow-inventory`   | Tracks and reserves inventory                    |
| `transitflow-dispatch`    | Manages shipment scheduling and dispatch         |
| `transitflow-delivery`    | Tracks and manages delivery lifecycle            |
| `transitflow-kafka-config`| Shared Kafka configuration (optional module)     |

---

## 🧱 Internal Architecture & Design Patterns

### 🧭 Layered Flow Inside Each Module


<p align="center">
  <img src="docs/Flowchart.png" alt="Layered Module Architecture" height="550">
</p>


> All modules follow a **layered hexagonal architecture** for separation of concerns, testability, and clean dependency flow.

---
🛠️ Architecture Overview
TransitFlow follows an event-driven architecture using Kafka to coordinate state transitions across loosely coupled services.

🔄 System Flow (Textual Summary)
1. 🧑‍💼 Client places an order via REST API.

2. 🧾 Order Service persists the order and publishes OrderCreatedEvent.

3. 📦 Inventory Service listens, reserves items, and publishes InventoryReservedEvent.

4. 🚚 Dispatch Service creates shipment and emits ShipmentDispatchedEvent.

5. 📍 Delivery Service tracks delivery and emits ShipmentDeliveredEvent.

All state transitions are triggered asynchronously via Kafka, never through direct service calls.

### 🗂️ Outbox Pattern (Order Module)

<p align="center">
  <img src="docs/flowchart2.png" alt="Outbox Pattern Sequence Diagram height="750">
</p>

> The **Outbox pattern** ensures events are reliably published by storing them in a DB outbox and processing them via a background publisher.




## 🧱 Module Structure

```
transitflow-order/
└── src/
    └── main/
        ├── java/com/transitflow/order/
        │   ├── adapter/in/web              ← REST Controllers
        │   ├── adapter/out/jpa             ← JPA Repositories & Entities
        │   ├── application/port/in         ← Service Interfaces
        │   ├── application/port/out        ← Repository Interfaces
        │   ├── application/service         ← Service Implementation
        │   ├── domain                      ← Domain Models
        │   ├── mapper                      ← MapStruct Mappers
        │   ├── messaging/publisher         ← Kafka Publisher
        │   └── validator                   ← Request Validators
        └── resources/
            └── application.yml
```

> 🧼 Clean Hexagonal Architecture — separating business logic, infrastructure, and delivery.

---

## 🔄 Event Chronology

```
ORDERED → RESERVED → DISPATCHED → DELIVERED
```

Each microservice **emits/consumes domain events** using **Apache Kafka** for asynchronous communication.

---

## 📦 Outbox Pattern

Used across services for **resilience** and **eventual consistency**:

- Persist domain event in the database within the same transaction.
- Periodic publisher reads from outbox table and emits to Kafka.
- Ensures fault tolerance across services.

---

## 🚛 Delivery Module Overview

| Component                 | Purpose                                     |
|--------------------------|---------------------------------------------|
| `ShipmentEvent`           | Tracks delivery lifecycle in DB             |
| `ShipmentEventRepository` | Access to delivery event history            |
| `DeliveryKafkaConfig`     | Kafka consumer setup for dispatch events    |
| `DeliveryService`         | Core business logic                         |
| `DeliveryEventListener`   | Consumes `ShipmentDispatchedEvent`          |
| `DeliveryEventFactory`    | Produces `ShipmentDeliveredEvent`           |
| `OutboxPublisher`         | Emits `ShipmentDeliveredEvent` to Kafka     |
| `DeliveryController`      | REST API for manual delivery ops            |

---

## 🐳 Docker-Based Dev Setup

```bash
# Make startup scripts executable
chmod +x transitflow-*/wait-for-mysql.sh

# Build artifacts
mvn clean package -DskipTests

# Start all services via Docker Compose
docker compose up -d --build
```

---

## ⚡ Quick Start

```bash
# 1️⃣ Clone the repo
git clone https://github.com/Shubh00796/TransitFlow-Event-Driven-Logistics-Platform.git
cd TransitFlow-Event-Driven-Logistics-Platform

# 2️⃣ Build & boot services
mvn clean package -DskipTests
docker compose up -d --build

# 3️⃣ Place an order
curl -X POST http://localhost:8081/api/orders \
     -H 'Content-Type: application/json' \
     -d '{"productId":1,"quantity":2}'
```

---

## 📚 Resources

- 📘 [Event-Driven Architecture — Martin Fowler](https://martinfowler.com/articles/201701-event-driven.html)
- 📘 [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- 📘 [Outbox Pattern — Microservices.io](https://microservices.io/patterns/data/application-events.html)

---

## 🤝 Contributing

We welcome your ideas and PRs! 🚀

```bash
# Fork + branch
git checkout -b feature/my-feature

# Commit & push changes
git commit -m "Add feature"
git push origin feature/my-feature

# Open a pull request
```

For large changes, create an issue first to discuss your proposal.

---

## 🙌 Support & Feedback

Found this useful?  
⭐ Star the repo or open an issue with suggestions.

---



### 🚚 Event Flow

```plaintext
OrderService → OrderCreatedEvent → InventoryService → InventoryReservedEvent → DispatchService → ShipmentDispatchedEvent → DeliveryService → ShipmentDeliveredEvent


