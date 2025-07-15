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

![TransitFlow Architecture](./docs/flowchart.png)


    
