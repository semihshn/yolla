workspace "Yolla transactional outbox" "OrderCreated event flow on transactional-outbox-pattern-implementation" {

    !identifiers flat

    model {

        customer = person "Order client" "Calls the order API"
        yolla = softwareSystem "Yolla" "Ordering and payment services" {

            ordering = container "Ordering API" "Accepts orders and publishes OrderCreated from an outbox" "Spring Boot / Java 17" {

                orderController = component "OrderController" "POST /ordering/v1/orders" "Spring MVC"
                orderService = component "OrderService" "Creates order and outbox record in one transaction" "Spring service"
                orderRepository = component "OrderRepositoryAdapter" "Persists order" "Spring Data JPA"
                outboxRepository = component "OutboxJpaRepositoryAdapter" "Persists and reads outbox rows" "Spring Data JPA"
                scheduler = component "OutboxProcessorAdapter" "Runs every 5 seconds" "Spring scheduler"
                outboxService = component "OutboxService" "Retries active outbox rows" "Spring service"
                publisher = component "EventPublisherAdapter" "Waits for Kafka send result" "Spring Kafka"
            
}

            orderingDb = container "Ordering MySQL" "orders and outbox_data tables" "MySQL" "Database"
            kafka = container "Kafka" "order-created topic; single local KRaft broker" "Apache Kafka" "Broker"
            payment = container "Payment API" "Consumes OrderCreated and records payment" "Spring Boot / Java 17" {

                listener = component "OrderingDomainEventListenerAdapter" "Consumes order-created and acknowledges after payment" "Spring Kafka"
                paymentService = component "PaymentService" "Marks payment completed" "Spring service"
                paymentRepository = component "PaymentRepositoryAdapter" "Persists payment" "Spring Data JPA"
            
}

            paymentDb = container "Payment MySQL" "payments table" "MySQL" "Database"
        
}


        customer -> ordering "Creates an order" "HTTP POST" {
 tags "Sync" 
}

        ordering -> orderingDb "Writes orders and outbox_data; reads and marks outbox" "JDBC" {
 tags "DataDependency" 
}

        ordering -> kafka "Publishes OrderCreated" "Kafka producer" {
 tags "Event" 
}

        kafka -> payment "Delivers OrderCreated" "Kafka consumer" {
 tags "Event" 
}

        payment -> paymentDb "Writes payments" "JDBC" {
 tags "DataDependency" 
}


        customer -> orderController "Submits order" "HTTP POST" {
 tags "Sync" 
}

        orderController -> orderService "Creates order" "Java" {
 tags "Sync" 
}

        orderService -> orderRepository "Persists order" "Java" {
 tags "Sync" 
}

        orderRepository -> orderingDb "Inserts orders row" "JDBC" {
 tags "DataDependency" 
}

        orderService -> outboxRepository "Persists event in same DB transaction" "Java" {
 tags "Sync" 
}

        outboxRepository -> orderingDb "Inserts, reads, and updates outbox_data" "JDBC" {
 tags "DataDependency" 
}

        scheduler -> outboxService "Runs retry every 5 seconds" "Java" {
 tags "Sync" 
}

        outboxService -> outboxRepository "Retrieves active rows; marks successful rows DELETED" "Java" {
 tags "Sync" 
}

        outboxService -> publisher "Publishes stored payload" "Java" {
 tags "Sync" 
}

        publisher -> kafka "Sends OrderCreated and waits for broker result" "Kafka producer" {
 tags "Event" 
}

        kafka -> listener "Delivers order-created record" "Kafka consumer" {
 tags "Event" 
}

        listener -> paymentService "Processes order event" "Java" {
 tags "Sync" 
}

        paymentService -> paymentRepository "Persists completed payment" "Java" {
 tags "Sync" 
}

        paymentRepository -> paymentDb "Inserts payments row" "JDBC" {
 tags "DataDependency" 
}

    
}


    views {

        systemContext yolla "context" "System context" {
 include *
 autoLayout lr 
}

        container yolla "containers" "All deployable services and stores" {
 include *
 autoLayout lr 
}

        container yolla "filter-base" "Base for relationship filters" {
 include *
}

        filtered "filter-base" include "Element,Sync" "sync-only" "Request and response traffic"
        filtered "filter-base" include "Element,Event" "event-only" "Kafka event traffic"
        filtered "filter-base" include "Element,DataDependency" "data-dependencies" "Database dependencies only"
        component ordering "ordering-components" "Ordering API internals" {
 include *
 autoLayout lr 
}

        component payment "payment-components" "Payment API internals" {
 include *
 autoLayout lr 
}


        dynamic yolla "outbox-flow" "Order to payment, numbered steps" {

            customer -> ordering "POST /ordering/v1/orders"
            ordering -> orderingDb "Commit orders and outbox_data atomically"
            ordering -> orderingDb "Scheduler reads active outbox_data"
            ordering -> kafka "Send OrderCreated; wait for broker acknowledgement"
            ordering -> orderingDb "Mark acknowledged outbox row DELETED"
            kafka -> payment "Deliver order-created record"
            payment -> paymentDb "Insert completed payment"
            autoLayout lr
        
}


        dynamic ordering "ordering-outbox-detail" "Ordering API outbox sequence" {

            customer -> orderController "POST /ordering/v1/orders"
            orderController -> orderService "Create order"
            orderService -> orderRepository "Save order in transaction"
            orderRepository -> orderingDb "INSERT orders"
            orderService -> outboxRepository "Save OrderCreated in same transaction"
            outboxRepository -> orderingDb "INSERT outbox_data"
            scheduler -> outboxService "Scheduled retry"
            outboxService -> outboxRepository "Read active outbox rows"
            outboxRepository -> orderingDb "SELECT outbox_data"
            outboxService -> publisher "Publish stored payload"
            publisher -> kafka "Send and await broker result"
            outboxService -> outboxRepository "On success, mark row DELETED"
            outboxRepository -> orderingDb "UPDATE outbox_data status"
            autoLayout lr
        
}


        dynamic payment "payment-consumer-detail" "Payment API event sequence" {

            kafka -> listener "Deliver OrderCreated"
            listener -> paymentService "Pay order"
            paymentService -> paymentRepository "Create completed payment"
            paymentRepository -> paymentDb "INSERT payments"
            autoLayout lr
        
}


        styles {

            element "Person" {
 shape Person
 background #34495e
 color #ffffff 
}

            element "Software System" {
 background #1f4b75
 color #ffffff 
}

            element "Container" {
 background #2471a3
 color #ffffff 
}

            element "Component" {
 background #5dade2
 color #102a43 
}

            element "Database" {
 shape Cylinder
 background #8d6e63
 color #ffffff 
}

            element "Broker" {
 shape Pipe
 background #2467a8
 color #ffffff 
}

            relationship "Sync" {
 color #27824a
 thickness 3 
}

            relationship "Event" {
 color #2467a8
 style Dashed
 thickness 3 
}

            relationship "DataDependency" {
 color #8d6e63
 thickness 3 
}

        
}

    
}


}

