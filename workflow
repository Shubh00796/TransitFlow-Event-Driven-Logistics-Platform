1. Create Entity (e.g., Order)
2. Create Repository (interface extending JpaRepository)
3. Create Service (business logic)
4. Create Controller (REST endpoints)
5. Integrate Kafka (listener or sender if needed)

//modules
1. transitflow-common     → shared DTOs & Enums
2. transitflow-order      → handles order creation
3. transitflow-inventory  → tracks and reserves inventory
4. transitflow-dispatch   → handles shipment & vehicle dispatching
5. transitflow-delivery   → handles delivery & tracking


//intigration flow
1. Order module: POST /orders → OrderService → DB + send Kafka event
2. Inventory module: Listens to event → reserves stock
3. Dispatch module: Dispatches shipment based on order
4. Delivery module: Receives delivery updates

ORDERED ➝ RESERVED ➝ DISPATCHED ➝ DELIVERED
Each state is transitioned by a module, not by calling each other, but by publishing and consuming events.


//CHRNOLOGICAL-ORDER
1. transitflow-common        ← shared DTOs/events/enums
2. transitflow-order         ← creates order + emits event
3. transitflow-inventory     ← reserves stock
4. transitflow-dispatch      ← assigns shipment
5. transitflow-delivery      ← tracks delivery
6. transitflow-kafka-config  ← shared Kafka bean config (optional)

Client (POST /api/orders)
  ⬇
OrderService
  📝 orders table
  📣 Kafka: OrderCreatedEvent
  ⬇
InventoryService
  📉 inventory_items table
  📣 Kafka: InventoryReservedEvent
  ⬇
DispatchService
  🚚 assign vehicle
  📝 shipments table
  📣 Kafka: ShipmentDispatchedEvent
  ⬇
DeliveryService
  📦 simulate delivery
  ✅ update status
  📣 Kafka: ShipmentDeliveredEvent


🧱 Layered Flow Inside Each Module

Controller → Validator → Service Interface → Impl → RepositoryService → JPA Repos
                                               ↓
                                           KafkaPublisher.


transitflow-order/
└── src/
    └── main/
        ├── java/com/transitflow/order/
        │   ├── adapter/in/web               ← REST Controllers
        │   ├── adapter/out/jpa              ← JPA Repositories & Entities
        │   ├── application/port/in          ← Service Interfaces
        │   ├── application/port/out         ← Repository Interfaces
        │   ├── application/service          ← ServiceImpl
        │   ├── domain                       ← Domain Models
        │   ├── mapper                       ← MapStruct
        │   ├── messaging/publisher          ← KafkaPublisher
        │   └── validator                    ← Request validators
        └── resources/
            └── application.yml



User Request
   │
   ▼
OrderService
   ├─ Save Order to DB
   └─ Save OutboxEvent to DB (same TX)
   ▲
   │
KafkaPublisher (Async)
   ├─ Poll PENDING events
   ├─ Send to Kafka
   └─ Mark as SENT or FAILED



[Client/API]
     │
     ▼
[OrderService: create/update]
     │  (DB TX commits)
     ├─ writes → orders
     └─ writes → outbox_event (PENDING)

     ── every 3s ──► [OutboxPublisherService]
                     ├─ fetch PENDING events
                     ├─ send to Kafka
                     └─ update outbox_event status to PUBLISHED/FAILED

     ──► [Kafka Broker (Topic: ordercreated)]
             └─▲
               │
     ┌─────────┴─────────┐
     │                   │
[InventoryService]   [ShippingService]  etc.



chmod +x transitflow-order/wait-for-mysql.sh
chmod +x transitflow-inventory/wait-for-mysql.sh
chmod +x transitflow-dispatch/wait-for-mysql.sh
chmod +x transitflow-delivery/wait-for-mysql.sh


//🚀 That’s the standard production dev loop used by experienced teams!
mvn clean package -DskipTests && docker compose up -d --build



sequenceDiagram
  participant User
  participant OrderService
  participant OutboxTable
  participant Kafka
  participant InventoryService

  User->>OrderService: Create Order API
  OrderService->>OutboxTable: Save OrderCreatedEvent (PENDING)
  OrderService->>Kafka: OutboxPublisher publishes event
  Kafka->>Kafka UI: Topic + Partition appear
  Kafka->>InventoryService: Delivers OrderCreatedEvent
  InventoryService->>InventoryDB: Update stock




  OrderService → OrderCreatedEvent → InventoryService → InventoryReservedEvent
  → DispatchService → ShipmentDispatchedEvent → DeliveryService → ShipmentDeliveredEvent



//docker comands to remove the unused imgaes
docker system prune -a --volumes
-a: remove all unused images (not just dangling)

--volumes: remove unused volumes (e.g. Kafka data dirs)

Step 2: Check What’s Eating Space
bash
Copy
Edit
docker system df


docker-compose down -v
This will:

Stop all containers

Remove all named/anonymous volumes

💡 Good for dev — resets DB and Kafka clean.

Clean Architecture Layers (Hybrid Setup)

┌────────────────────────────────────────────┐
│            External APIs (Async)           │
│     (Called via WebClient from Services)   │
└────────────────────────────────────────────┘
            ↓
┌────────────────────────────────────────────┐
│         Infrastructure Layer               │
│  - WebClientConfig                         │
│  - ExternalApiService (uses WebClient)     │
│  - JPA Repositories                        │
└────────────────────────────────────────────┘
            ↓
┌────────────────────────────────────────────┐
│         Application Layer                  │
│  - Service Interfaces                      │
│  - Business Logic                          │
│  - Async Wrappers (e.g., Mono.toFuture())  │
└────────────────────────────────────────────┘
            ↓
┌────────────────────────────────────────────┐
│         Presentation Layer (MVC)           │
│  - Controllers (Spring MVC)                │
│  - DTOs / Response Models                  │
└────────────────────────────────────────────┘
            ↓
┌────────────────────────────────────────────┐
│         Domain Layer                       │
│  - Entities                                │
│  - Value Objects                           │
└────────────────────────────────────────────┘




//✅ Structure of a Good Commit Message (One-Liner)
<type>(<scope>): <what> and <why (optional)>
✅ Common <type> prefixes (from Conventional Commits):
Type	Use Case
feat	New feature
fix	Bug fix
refactor	Code restructuring, no behavior change
chore	Non-functional task (build, config, cleanup)
test	Adding or updating tests
docs	Documentation changes
style	Code formatting, no logic changes
perf	Performance improvement