workspace "Yolla - idempotent-consumer-with-redis" "Outbox delivery and Redis lock" {
!identifiers flat
model {
client = person "Order client" "Calls the order API"
yolla = softwareSystem "Yolla" "Ordering and payment demos" {
ordering = container "Ordering API" "Creates orders and publishes OrderCreated" "Spring Boot / Java 17"
orderingDb = container "Ordering MySQL" "orders and outbox_data tables" "MySQL" "Database"
kafka = container "Kafka" "order-created topic; local KRaft broker" "Apache Kafka" "Broker"
payment = container "Payment API" "Consumes OrderCreated and records payment" "Spring Boot / Java 17"
paymentDb = container "Payment MySQL" "payments and processed events" "MySQL" "Database"
redis = container "Redis" "30-second order lock keys" "Redis" "Database"
}
client -> ordering "Submits order" "HTTP POST" {
    tags "Sync"
}
ordering -> orderingDb "Writes and reads orders and outbox" "JDBC" {
    tags "DataDependency"
}
ordering -> kafka "Publishes OrderCreated" "Kafka producer" {
    tags "Event"
}
kafka -> payment "Delivers OrderCreated" "Kafka consumer" {
    tags "Event"
}
payment -> paymentDb "Persists payment" "JDBC" {
    tags "DataDependency"
}
payment -> redis "SETNX order lock with TTL" "Redis" {
    tags "DataDependency"
}
}
views {
dynamic yolla "flow" "Outbox delivery and Redis lock" {
    client -> ordering "POST /ordering/v1/orders"
    ordering -> orderingDb "Save order and outbox row in one transaction"
    ordering -> orderingDb "Scheduler reads active outbox rows"
    ordering -> kafka "Call KafkaTemplate.send for OrderCreated"
    ordering -> orderingDb "Mark outbox row processed after send call"
    kafka -> payment "Deliver order-created record"
    payment -> redis "Acquire order lock with SETNX and 30-second TTL"
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
