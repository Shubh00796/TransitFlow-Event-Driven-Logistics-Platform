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


