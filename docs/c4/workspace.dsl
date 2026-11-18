workspace "Yolla - outbox-with-annotation" "Spring Modulith event publication flow" {
!identifiers flat
model {
client = person "Order client" "Calls the order API"
yolla = softwareSystem "Yolla" "Ordering and payment demos" {
ordering = container "Ordering API" "Publishes @Externalized OrderCreated via Spring Modulith" "Spring Boot / Java 17"
orderingDb = container "Ordering MySQL" "orders and EVENT_PUBLICATION tables" "MySQL" "Database"
kafka = container "Kafka" "order-created topic; local KRaft broker" "Apache Kafka" "Broker"
payment = container "Payment API" "Consumes OrderCreated" "Spring Boot / Java 17"
paymentDb = container "Payment MySQL" "payments table" "MySQL" "Database"
}
client -> ordering "Submits order" "HTTP POST" {
    tags "Sync"
}
ordering -> orderingDb "Stores order and event publication" "JDBC" {
    tags "DataDependency"
}
ordering -> kafka "Externalizes OrderCreated" "Kafka producer" {
    tags "Event"
}
kafka -> payment "Delivers OrderCreated" "Kafka consumer" {
    tags "Event"
}
payment -> paymentDb "Persists payment" "JDBC" {
    tags "DataDependency"
}
}
views {
dynamic yolla "flow" "Spring Modulith event publication flow" {
    client -> ordering "POST /ordering/v1/orders"
    ordering -> orderingDb "Commit order and EVENT_PUBLICATION entry"
    ordering -> kafka "Externalize @Externalized OrderCreated"
    kafka -> payment "Deliver order-created record"
    payment -> paymentDb "Insert payment"
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
