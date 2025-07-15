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

![Layered Module Architecture](docs/Flowchart.png)

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

![Outbox Pattern Sequence Diagram](docs/flowchart2.png)

> The **Outbox pattern** ensures events are reliably published by storing them in a DB outbox and processing them via a background publisher.

🧱 Example Module Structure (transitflow-order)
transitflow-order/
└── src/
    └── main/
        ├── java/com/transitflow/order/
        │   ├── adapter/in/web               ← REST Controllers
        │   ├── adapter/out/jpa              ← JPA Repositories & Entities
        │   ├── application/port/in          ← Service Interfaces
        │   ├── application/port/out         ← Repository Interfaces
        │   ├── application/service          ← Service Implementation
        │   ├── domain                       ← Domain Models
        │   ├── mapper                       ← MapStruct Mappers
        │   ├── messaging/publisher          ← Kafka Publisher
        │   └── validator                    ← Request Validators
        └── resources/
            └── application.yml

🔄 Event Chronology (Kafka)
ORDERED ➝ RESERVED ➝ DISPATCHED ➝ DELIVERED
Each service emits or consumes domain events via Kafka:
| Event                     | Publisher         | Consumer          |
| ------------------------- | ----------------- | ----------------- |
| `OrderCreatedEvent`       | Order Service     | Inventory Service |
| `InventoryReservedEvent`  | Inventory Service | Dispatch Service  |
| `ShipmentDispatchedEvent` | Dispatch Service  | Delivery Service  |
| `ShipmentDeliveredEvent`  | Delivery Service  | Order Service     |

📦 Outbox Pattern (Resilience & Reliability)
All services use the Outbox Pattern to safely publish events:

Persist event in DB in the same transaction.

Outbox publisher reads and publishes to Kafka.

Ensures eventual consistency and fault tolerance.

✅ Delivery Module Components
| Component                 | Purpose                                     |
| ------------------------- | ------------------------------------------- |
| `ShipmentEvent`           | Entity to track delivery steps in DB        |
| `ShipmentEventRepository` | DB access for delivery history              |
| `DeliveryKafkaConfig`     | Kafka consumer config for dispatch events   |
| `DeliveryService`         | Core delivery business logic                |
| `DeliveryEventListener`   | Consumes `ShipmentDispatchedEvent`          |
| `DeliveryEventFactory`    | Creates `ShipmentDeliveredEvent`            |
| `OutboxPublisher`         | Publishes `ShipmentDeliveredEvent` to Kafka |
| `DeliveryController`      | REST API for manual delivery operations     |


 🐳 Docker-Based Local Development
 🔧 Make startup scripts executable:
   chmod +x transitflow-*/wait-for-mysql.sh
🚀 Build and run all services:
   mvn clean package -DskipTests
  docker compose up -d --build


  ⚡ Quick Start
# 1. Clone the repository
git clone https://github.com/Shubh00796/TransitFlow-Event-Driven-Logistics-Platform.git
cd TransitFlow-Event-Driven-Logistics-Platform

# 2. Build and start services
mvn clean package -DskipTests
docker compose up -d --build

# 3. Place an order
curl -X POST http://localhost:8081/api/orders \
     -H 'Content-Type: application/json' \
     -d '{"productId":1,"quantity":2}'


📚 Further Reading
📘 Event-Driven Architecture - Martin Fowler

📘 Kafka Documentation

📘 Outbox Pattern - Microservices.io

🤝 Contributing
We welcome contributions! 🚀
To contribute:

Fork the repo

Create your branch (git checkout -b feature/xyz)

Commit your changes

Open a pull request

For major changes, please open an issue first to discuss your ideas.



🙌 Support & Feedback
If you find this useful, consider starring ⭐ the repo or opening an issue to suggest improvements.


### 🚚 Event Flow

```plaintext
OrderService → OrderCreatedEvent → InventoryService → InventoryReservedEvent → DispatchService → ShipmentDispatchedEvent → DeliveryService → ShipmentDeliveredEvent


