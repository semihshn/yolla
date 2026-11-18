workspace "Yolla - main" "Application skeleton and configured MySQL dependencies" {
!identifiers flat
model {
yolla = softwareSystem "Yolla" "Ordering and payment demos" {
inventory = container "Inventory API" "Spring Boot application skeleton" "Spring Boot / Java 17"
inventoryDb = container "Inventory MySQL" "inventory schema" "MySQL" "Database"
ordering = container "Ordering API" "Spring Boot application skeleton" "Spring Boot / Java 17"
orderingDb = container "Ordering MySQL" "ordering schema" "MySQL" "Database"
payment = container "Payment API" "Spring Boot application skeleton" "Spring Boot / Java 17"
paymentDb = container "Payment MySQL" "payment schema" "MySQL" "Database"
shipping = container "Shipping API" "Spring Boot application skeleton" "Spring Boot / Java 17"
shippingDb = container "Shipping MySQL" "shipping schema" "MySQL" "Database"
}
inventory -> inventoryDb "Configured datasource" "JDBC" {
    tags "DataDependency"
}
ordering -> orderingDb "Configured datasource" "JDBC" {
    tags "DataDependency"
}
payment -> paymentDb "Configured datasource" "JDBC" {
    tags "DataDependency"
}
shipping -> shippingDb "Configured datasource" "JDBC" {
    tags "DataDependency"
}
}
views {
container yolla "topology" "Application skeleton and configured MySQL dependencies" {
    include *
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
