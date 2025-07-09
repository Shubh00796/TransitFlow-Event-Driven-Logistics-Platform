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


//CHRNOLOGICAL-ORDER
1. transitflow-common        ← shared DTOs/events/enums
2. transitflow-order         ← creates order + emits event
3. transitflow-inventory     ← reserves stock
4. transitflow-dispatch      ← assigns shipment
5. transitflow-delivery      ← tracks delivery
6. transitflow-kafka-config  ← shared Kafka bean config (optional)

🧱 Layered Flow Inside Each Module

Controller → Validator → Service Interface → Impl → RepositoryService → JPA Repos
                                               ↓
                                           KafkaPublisher


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




