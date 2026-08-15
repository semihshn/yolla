# Place Order: Ports and Adapters

Bu modül, tek bir `Place Order` use case'i üzerinden hexagonal architecture akışını gösterir.

## Akış

`OrderController` HTTP isteğini alır, `PlaceOrderRequest.toModel()` ile domain komutuna dönüştürür ve `OrderFacade`'e devreder.

İstek biçimi ve zorunlu alanlar `PlaceOrderRequest` üzerindeki `jakarta.validation` constraint'leriyle kontrol edilir. Domain katmanındaki validation servisi ise stok yeterliliği, ödeme yöntemi desteği ve ödeme onayı gibi iş kurallarını kontrol eder.

`PlaceOrderService` iş akışını yürütür. Stok, ödeme, kayıt ve bildirim gibi dış dünya bağımlılıklarını yalnızca domain içinde tanımlanan port'lar üzerinden kullanır:

- `StockPort`
- `PaymentPort`
- `OrderRepository`
- `NotificationPort`

Gerçek adapter'lar `infra` modülündedir. Demo bağımsız çalışabilsin diye stok ve kayıt için in-memory, ödeme ve bildirim için basit adapter'lar kullanılır.

HTTP response modeli bilinçli olarak tek ve generic olmayan `Response` sınıfıdır; başarılı sonuç `data`, hatalar ise `errorCode` ve `errorDescription` alanlarıyla döner.

## Paket yapısı

```text
ordering-api/
├── domain/
│   └── src/main/java/com/yolla/orderingapi/order/
│       ├── model/
│       ├── service/
│       ├── validation/
│       └── *Port.java
└── infra/
    └── src/main/java/com/yolla/orderingapi/
        ├── adapter/order/
        ├── adapter/payment/
        ├── adapter/persistence/
        ├── adapter/stock/
        └── common/rest/
```

## Testler

Domain testi Spring veya veritabanı başlatmadan port stub'larıyla çalışır. Stub sınıfları `domain/src/test/.../stub` altında tutulur; test sınıfları yalnızca use case davranışına odaklanır.

```bash
JAVA_HOME=/path/to/jdk-17 ./gradlew :ordering-api:domain:test :ordering-api:infra:test
```

## Örnek istek

```bash
curl -X POST http://localhost:8010/orders \
  -H 'Content-Type: application/json' \
  -d '{
    "customerId": "customer-1",
    "items": [
      {"productId": "product-1", "quantity": 2, "unitPrice": 14.99}
    ],
    "paymentMethod": "CARD"
  }'
```
