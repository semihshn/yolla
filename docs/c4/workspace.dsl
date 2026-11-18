workspace "Yolla - why-should-use-transactional-outbox-pattern" "Failure after order save and before Kafka send" {
!identifiers flat
model {
client = person "Order client" "Calls the order API"
yolla = softwareSystem "Yolla" "Ordering and payment demos" {
ordering = container "Ordering API" "Direct publish demo with a forced exception" "Spring Boot / Java 17" {
    controller = component "OrderController" "Receives POST /ordering/v1/orders" "Spring MVC"
    service = component "OrderService" "Persists order before attempting publish" "Spring service"
    repository = component "OrderRepositoryAdapter" "Saves order" "Spring Data JPA"
    publisher = component "EventPublisherAdapter" "Throws before KafkaTemplate.send in this demo" "Spring Kafka"
}
orderingDb = container "Ordering MySQL" "orders table" "MySQL" "Database"
kafka = container "Kafka" "order-created topic; no event sent in failure path" "Apache Kafka" "Broker"
payment = container "Payment API" "Consumes order-created if delivered" "Spring Boot / Java 17"
paymentDb = container "Payment MySQL" "payments table" "MySQL" "Database"
}
ordering -> orderingDb "Writes order" "JDBC" {
    tags "DataDependency"
}
ordering -> kafka "Configured direct publish path" "Kafka producer" {
    tags "Event"
}
kafka -> payment "Configured consumer path" "Kafka consumer" {
    tags "Event"
}
payment -> paymentDb "Writes payment if event arrives" "JDBC" {
    tags "DataDependency"
}
client -> controller "Submits order" "HTTP POST" {
    tags "Sync"
}
controller -> service "Creates order" "Java" {
    tags "Sync"
}
service -> repository "Persists order" "Java" {
    tags "Sync"
}
repository -> orderingDb "Inserts orders row" "JDBC" {
    tags "DataDependency"
}
service -> publisher "Attempts event publication" "Java" {
    tags "Sync"
}
}
views {
dynamic ordering "flow" "Failure after order save and before Kafka send" {
    client -> controller "POST /ordering/v1/orders"
    controller -> service "Create order"
    service -> repository "Save order without @Transactional"
    repository -> orderingDb "Commit orders row"
    service -> publisher "Forced exception before KafkaTemplate.send"
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
