workspace "Yolla - hexagonal-demo" "Hexagonal place-order flow with in-memory adapters" {
!identifiers flat
model {
client = person "Order client" "Calls the order API"
yolla = softwareSystem "Yolla" "Ordering and payment demos" {
ordering = container "Ordering API" "Hexagonal order demo with in-memory adapters" "Spring Boot / Java 17" {
    controller = component "OrderController" "Receives POST /orders" "Spring MVC"
    service = component "PlaceOrderService" "Validates and places order" "Spring service"
    stock = component "Stock check" "InMemoryStockAdapter checks available stock" "In-memory adapter"
    paymentFake = component "Payment stub" "FakePaymentAdapter approves a supported method" "In-memory adapter"
    repository = component "Order store" "InMemoryOrderRepositoryAdapter stores the order" "In-memory adapter"
    notification = component "Notification" "LoggingNotificationAdapter logs confirmation" "Logging adapter"
}
}
client -> controller "Submits order" "HTTP POST" {
    tags "Sync"
}
controller -> service "Places order" "Java" {
    tags "Sync"
}
service -> stock "Checks stock" "Java" {
    tags "Sync"
}
service -> paymentFake "Authorizes payment" "Java" {
    tags "Sync"
}
service -> repository "Stores order" "Java" {
    tags "Sync"
}
service -> notification "Notifies confirmation" "Java" {
    tags "Sync"
}
}
views {
dynamic ordering "flow" "Hexagonal place-order flow with in-memory adapters" {
    client -> controller "POST /orders"
    controller -> service "Place order"
    service -> stock "Check stock availability"
    service -> paymentFake "Check method and authorize payment"
    service -> repository "Store confirmed order in memory"
    service -> notification "Log order confirmation"
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
