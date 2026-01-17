# Distributed Tracing with OpenTelemetry, Tempo & Grafana

This project demonstrates distributed tracing across Spring Boot microservices using OpenTelemetry, Grafana Tempo, and Grafana.

## Architecture

```
┌─────────────────┐         ┌─────────────────┐
│  Product Service│◄───────►│  Order Service  │
│    (port 9002)  │  HTTP   │   (port 9003)   │
└────────┬────────┘         └────────┬────────┘
         │                           │
         │    OTLP (HTTP/Protobuf)   │
         │         port 4318         │
         └───────────┬───────────────┘
                     ▼
            ┌─────────────────┐
            │      Tempo      │
            │   (port 3200)   │
            │  Trace Storage  │
            └────────┬────────┘
                     │
                     ▼
            ┌─────────────────┐
            │     Grafana     │
            │   (port 3000)   │
            │  Visualization  │
            └─────────────────┘
```

## Components

| Component | Port | Description |
|-----------|------|-------------|
| Product Service | 9002 | Manages products, calls Order Service |
| Order Service | 9003 | Manages orders, calls Product Service |
| Tempo | 3200 (API), 4318 (OTLP) | Distributed tracing backend |
| Grafana | 3000 | Visualization and trace exploration |
| PostgreSQL | 5991 | Product database |
| PostgreSQL | 5992 | Order database |

## How Tracing Works

### 1. Trace Context Propagation

When a request enters a service, OpenTelemetry:
1. Creates a **Trace ID** (unique identifier for the entire request flow)
2. Creates a **Span ID** (unique identifier for each operation)
3. Propagates context via HTTP headers (`traceparent`, `tracestate`)

```
Client Request
     │
     ▼
┌─────────────────────────────────────────────────────┐
│ Product Service                                     │
│ ┌─────────────────────────────────────────────────┐ │
│ │ Span: GET /products/{id}/orders                 │ │
│ │ TraceID: abc123                                 │ │
│ │ SpanID: span-1                                  │ │
│ └──────────────────────┬──────────────────────────┘ │
└────────────────────────┼────────────────────────────┘
                         │ HTTP + traceparent header
                         ▼
┌─────────────────────────────────────────────────────┐
│ Order Service                                       │
│ ┌─────────────────────────────────────────────────┐ │
│ │ Span: GET /orders?productId={id}                │ │
│ │ TraceID: abc123 (same!)                         │ │
│ │ SpanID: span-2                                  │ │
│ │ ParentSpanID: span-1                            │ │
│ └─────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────┘
```

### 2. OpenTelemetry Spring Boot Starter

The `opentelemetry-spring-boot-starter` (BOM 2.23.0) provides automatic instrumentation for:
- **Incoming HTTP requests** (Spring MVC)
- **Outgoing HTTP requests** (RestClient, RestTemplate, WebClient)
- **JDBC database calls**
- **JPA/Hibernate operations**

No code changes required - instrumentation happens at bytecode level.

### 3. Trace Export

Traces are exported to Tempo via OTLP HTTP protocol:

```yaml
otel:
  exporter:
    otlp:
      endpoint: http://localhost:4318
      protocol: http/protobuf
```

## Project Structure

```
OpenTelemetry/
├── docker-compose.yml          # Tempo + Grafana setup
├── tempo.yaml                  # Tempo configuration
├── grafana/
│   └── provisioning/
│       └── datasources/
│           └── datasources.yaml  # Auto-configure Tempo datasource
├── product-service/
│   ├── build.gradle
│   └── src/main/
│       ├── java/.../
│       │   ├── config/
│       │   │   └── RestClientConfig.java
│       │   ├── controller/
│       │   ├── service/
│       │   └── client/
│       │       └── OrderClient.java      # Calls Order Service
│       └── resources/
│           └── application.yml
└── order-service/
    ├── build.gradle
    └── src/main/
        ├── java/.../
        │   ├── config/
        │   │   └── RestClientConfig.java
        │   ├── controller/
        │   ├── service/
        │   └── client/
        │       └── ProductClient.java    # Calls Product Service
        └── resources/
            └── application.yml
```

## Configuration

### build.gradle

```gradle
dependencies {
    // OpenTelemetry auto-instrumentation
    implementation 'io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter'
}

dependencyManagement {
    imports {
        mavenBom("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:2.23.0")
    }
}
```

### application.yml

```yaml
spring:
  application:
    name: <service-name>    # Used as service name in traces

otel:
  sdk:
    disabled: false
  exporter:
    otlp:
      endpoint: http://localhost:4318
      protocol: http/protobuf
  resource:
    attributes:
      service.name: <service-name>
      service.version: 1.0.0
      deployment.environment: development
  traces:
    exporter: otlp
  metrics:
    exporter: none
  logs:
    exporter: none

logging:
  pattern:
    level: "%5p [${spring.application.name:},%X{trace_id:-},%X{span_id:-}]"
```

## Running the Project

### 1. Start Infrastructure

```bash
# Start Tempo and Grafana
docker compose up -d

# Verify services are running
docker compose ps
```

### 2. Start Databases (if not running)

```bash
# Product database
docker run -d --name product-db \
  -e POSTGRES_DB=product_db \
  -e POSTGRES_USER=product \
  -e POSTGRES_PASSWORD=product \
  -p 5991:5432 postgres:16-alpine

# Order database
docker run -d --name order-db \
  -e POSTGRES_DB=order_db \
  -e POSTGRES_USER=order \
  -e POSTGRES_PASSWORD=order \
  -p 5992:5432 postgres:16-alpine
```

### 3. Start Microservices

```bash
# Terminal 1 - Product Service
cd product-service
./gradlew bootRun

# Terminal 2 - Order Service
cd order-service
./gradlew bootRun
```

### 4. Generate Traces

Make API calls to generate traces:

```bash
# Get all products
curl http://localhost:9002/products

# Get product with orders (cross-service call)
curl http://localhost:9002/products/1/orders

# Create order
curl -X POST http://localhost:9003/orders \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 5}'
```

## Viewing Traces in Grafana

### 1. Access Grafana

Open http://localhost:3000 in your browser.
- Username: `admin`
- Password: `admin`

### 2. Explore Traces

1. Click **Explore** (compass icon) in the left sidebar
2. Select **Tempo** datasource from dropdown
3. Use **Search** tab to find traces:
   - By **Service Name**: `product-service` or `order-service`
   - By **Span Name**: `GET /products`
   - By **Duration**: Find slow requests
   - By **Tags**: Filter by custom attributes

### 3. Understanding the Trace View

```
Trace Timeline View:

product-service: GET /products/1/orders  ████████████████████████  150ms
    └── order-service: GET /orders          ████████████████  120ms
            └── PostgreSQL: SELECT              ████████  80ms
```

- **Waterfall view**: Shows timing of each span
- **Span details**: Click any span to see:
  - HTTP method, URL, status code
  - Database queries
  - Exception details (if any)
  - Custom attributes

### 4. Enable Auto-Refresh

Click the refresh dropdown (top-right) and select an interval (5s, 10s, etc.)

## Trace Data Model

### Span Attributes

OpenTelemetry automatically captures:

| Attribute | Description |
|-----------|-------------|
| `http.method` | GET, POST, PUT, DELETE |
| `http.url` | Full request URL |
| `http.status_code` | Response status code |
| `http.route` | Route pattern (e.g., `/products/{id}`) |
| `db.system` | Database type (postgresql) |
| `db.statement` | SQL query |
| `exception.type` | Exception class name |
| `exception.message` | Error message |

### Custom Spans (Optional)

Add custom spans in your code:

```java
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

@Service
public class ProductService {

    private final Tracer tracer;

    public Product processProduct(Long id) {
        Span span = tracer.spanBuilder("processProduct")
            .setAttribute("product.id", id)
            .startSpan();
        try {
            // Business logic
            return product;
        } finally {
            span.end();
        }
    }
}
```

## Troubleshooting

### No traces appearing in Grafana

1. Check Tempo is running:
   ```bash
   docker compose ps
   docker compose logs tempo
   ```

2. Verify OTLP endpoint is reachable:
   ```bash
   curl -v http://localhost:4318/v1/traces
   ```

3. Check application logs for trace IDs in log output

### Traces not correlated across services

Ensure `RestClient` is created using `RestClient.builder()` - OpenTelemetry instruments this automatically.

### Permission denied errors in Tempo

```bash
docker compose down
docker volume rm opentelemetry_tempo-data
docker compose up -d
```

## Service URLs

| Service | URL |
|---------|-----|
| Product Service API | http://localhost:9002 |
| Order Service API | http://localhost:9003 |
| Grafana UI | http://localhost:3000 |
| Tempo API | http://localhost:3200 |
| OTLP Endpoint | http://localhost:4318 |
