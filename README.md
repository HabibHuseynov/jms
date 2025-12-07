# 🚀 JMS Demo with Spring Boot & ActiveMQ Artemis

> A minimal, hands-on Spring Boot application showcasing modern JMS patterns with Apache ActiveMQ Artemis.

---

## ✨ What This Project Does

This is a **lightweight learning project** that shows you how to:

- 📨 Send and receive messages via JMS
- ⚡ Handle priority queues with Quality of Service (QoS) settings
- 🔄 Implement synchronous request–reply patterns
- 🏷️ Attach custom metadata headers to messages
- 🎯 Build a REST API that produces/consumes JMS messages

Perfect for understanding JMS concepts without the complexity!

---

## 🛠️ Tech Stack

| Component | Version |
|-----------|---------|
| **Java** | 21+ |
| **Spring Boot** | 4.x |
| **JMS Provider** | Apache ActiveMQ Artemis |
| **Build Tool** | Gradle |
| **Container** | Docker Compose |

---

## 📦 What's Inside

### REST Endpoints (`/api/orders`)

| Method | Endpoint | What It Does |
|--------|----------|--------------|
| `POST` | `/simple` | 🎯 Send a simple fire-and-forget order |
| `POST` | `/priority` | ⭐ Send with QoS: TTL, priority, delay |
| `POST` | `/with-metadata?region=EU` | 🏷️ Add custom headers (source, region, etc.) |
| `GET` | `/receive` | 📥 Pull one order from queue |
| `POST` | `/process` | 🔄 Request–reply (sync) processing |
| `POST` | `/bulk-express` | 🚚 Send bulk orders to express queue |

### Key Files

```
src/main/java/com/example/jms/
├── JmsApplication.java              ← Spring Boot entry point
├── controller/
│   └── OrderController.java          ← REST endpoints
├── service/
│   └── OrderMessagingService.java    ← JMS messaging logic
└── config/
    └── ActiveMqConfig.java           ← Queue bean definitions

src/main/resources/
└── application.yaml                  ← Configuration

docker-compose.yaml                   ← Artemis broker setup
test.http                             ← Ready-to-run HTTP requests
```

---

## 🚀 Quick Start

### 1️⃣ Prerequisites

Make sure you have:

- ✅ **Java 21** or compatible runtime
- ✅ **Docker** and **Docker Compose**
- ✅ **Gradle** (wrapper provided: `./gradlew`)

### 2️⃣ Start the Message Broker

From your project root:

```bash
docker compose -f docker-compose.yaml up -d
```

✨ **Broker is ready when:**
- JMS endpoint: `tcp://localhost:61616`
- Web console: http://localhost:8161 (admin / admin)

### 3️⃣ Run the Application

**Option A: Direct with Gradle**
```bash
./gradlew bootRun
```

**Option B: Build and run as JAR**
```bash
./gradlew clean build
java -jar build/libs/jms-0.0.1-SNAPSHOT.jar
```

🎉 **App is running at:** http://localhost:8080

---

## ⚙️ Configuration

Default settings in `application.yaml`:

```yaml
spring.artemis.mode: native
spring.artemis.broker-url: tcp://localhost:61616
spring.artemis.user: admin
spring.artemis.password: admin
server.port: 8080
```

> **Note:** ActiveMQ Artemis auto-creates queues on first use. If needed, manage them via the web console.

---

## 📨 Sample Order JSON

```json
{
  "orderId": "ORD-12345",
  "customerId": "CUST-789",
  "amount": 149.99,
  "status": "PENDING",
  "timestamp": "2024-12-07T10:30:00"
}
```

---

## 🧪 Testing Your Endpoints

### Option 1: Use the Included HTTP Collection ⭐

**File:** `test.http`

Open in:
- 🔧 **IntelliJ IDEA** — built-in HTTP client
- 🔧 **VS Code** — with [REST Client](https://marketplace.visualstudio.com/items?itemName=humao.rest-client) extension

Click the gutter icons to execute requests!

### Option 2: Use cURL

```bash
# Send a simple order
curl -X POST http://localhost:8080/api/orders/simple \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-001","customerId":"CUST-001","amount":99.99}'

# Receive an order
curl -X GET http://localhost:8080/api/orders/receive

# Send with metadata
curl -X POST "http://localhost:8080/api/orders/with-metadata?region=EU" \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-002","customerId":"CUST-002","amount":199.99}'
```

### Option 3: Use Postman or Similar Tools

Import the endpoints from the table above and test away!

---

## 🔍 Understanding the Response Codes

| Code | Meaning |
|------|---------|
| `200` | ✅ Success — message sent or processed |
| `204` | ℹ️ No Content — no messages available in queue (try sending one first!) |
| `400` | ❌ Bad request — check your JSON |
| `500` | ❌ Error — broker unreachable or request-reply timeout |

---

## 🐛 Troubleshooting

### ❌ "Can't connect to broker"

- Verify the broker started: `docker ps` should show the Artemis container
- Check ports aren't blocked: `61616` (JMS) and `8161` (console)
- Verify credentials in `application.yaml` match the broker (`admin/admin`)

### ❌ `/receive` returns 204 (No Content)

- This is normal! It means the queue is empty
- **Fix:** Send a message first using `/simple`, then try `/receive` again

### ❌ `/process` times out

- The request-reply expects a consumer on `order-processor` queue
- This demo shows the **client side only**
- You'd need to run a separate processor service to handle replies

### ❌ Other issues?

1. Check broker logs: `docker logs <container-id>`
2. Visit the broker console: http://localhost:8161
3. Verify `application.yaml` configuration

---

## 🏗️ Architecture Notes

```
Your App (Spring Boot)
    ↓
OrderController (REST endpoints)
    ↓
OrderMessagingService (JMS logic)
    ↓
ActiveMQ Artemis (Message Broker)
    ↓
Queues (order-queue, notification-queue, express-orders, etc.)
```

**Key Queues:**
- `order-queue` — Default order destination
- `order-processor` — Request-reply processor
- `express-orders` — High-priority orders
- `notification-queue` — Notifications

> ActiveMQ creates queues automatically on first use. Use the web console to manage them manually if needed.

---

## 📚 Build & Test

```bash
# Build the project
./gradlew clean build

# Run unit tests
./gradlew test

# Run and watch logs
./gradlew bootRun --info
```

---

## 🎯 Example Workflow

1. **Start broker** → `docker compose up -d`
2. **Start app** → `./gradlew bootRun`
3. **Send an order** → `POST /simple`
4. **Check the broker console** → http://localhost:8161
5. **Receive the order** → `GET /receive`
6. **See the message in queue stats!** 📊

---

## 📖 Learning Path

**Beginner:**
- Start with `/simple` endpoint
- Use `/receive` to pull messages
- Check the broker console to see messages flowing

**Intermediate:**
- Try `/priority` with QoS settings
- Experiment with `/with-metadata` custom headers
- Monitor queue behavior in the console

**Advanced:**
- Implement your own consumer service
- Build a replier for the `/process` endpoint
- Add more sophisticated message routing

---

## 📝 License

MIT — Feel free to use, modify, and adapt for your projects!

---

## ❓ Questions?

- 📖 Check [Spring for Apache ActiveMQ Artemis](https://spring.io/projects/spring-boot) docs
- 🔗 Explore [ActiveMQ Artemis Documentation](https://activemq.apache.org/artemis/)
- 💡 Review the code in `OrderController` and `OrderMessagingService` for patterns

---

**Happy messaging! 🚀💬**