# TransitFlow-Event-Driven-Logistics-Platform
EDA-Based Logistics Platform Backend Architecture with Kafka and Spring Boot


Overview: TransitFlow is a backend-only system for coordinating shipment orders, inventory, dispatch and delivery in a logistics context. It uses an event-driven architecture (EDA): key actions (like “OrderPlaced”, “InventoryReserved”, “ShipmentDispatched”) are represented as events that services publish to Kafka topics and other services consume asynchronously
confluent.io
devcenter.heroku.com
. This decoupling lets each component evolve and scale independently. TransitFlow’s goals include real-time coordination of orders, stock and vehicles, high scalability, and a clean modular design. Key features include:
Order Processing: Accept new shipment orders (origin, destination, items) and persist them. Publish an OrderCreated event.
Inventory Management: Listen for OrderCreated events, reserve stock for the order (update the inventory_items table), then publish InventoryReserved or InventoryOutOfStock.
Dispatch Coordination: Consume InventoryReserved events, allocate a vehicle/driver based on capacity and location, record a shipment record, and publish ShipmentDispatched.
Delivery Tracking: Consume ShipmentDispatched and update shipment status. On completion, emit ShipmentDelivered.
Analytics: Process event streams (e.g. with a separate service) to compute metrics like average delivery time.
Each of these flows is implemented as a separate microservice (or module) so that communications occur only via Kafka events. This illustrates core EDA skills (publish/subscribe, asynchronous handling, event modeling) in a realistic logistics domain.
