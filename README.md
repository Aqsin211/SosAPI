# SilentSignals

SilentSignals is a **secure, real-time panic alert system** that allows users to silently trigger SOS signals during emergencies. Alerts are instantly delivered to trusted contacts via WebSocket, and if contacts are offline, fallback notifications are sent via **email**. Built with **Redis**, **PostgreSQL**, and **JWT-secured APIs**, the system ensures reliable delivery, rate limiting, and detailed logging for each alert.

---

## Features

- **Panic Trigger Endpoint:** Users can silently trigger an SOS via `POST /api/sos/send`. Alerts broadcast instantly via WebSocket to trusted contacts.
- **Real-Time Alert Delivery:** Trusted contacts receive SOS alerts immediately over WebSocket (STOMP).
- **Fallback Notifications via Email:** If no contact responds within 3 minutes, fallback email alerts are sent to ensure delivery.
- **Trusted Contact Management:** Users manage trusted contacts (add, remove, view) identified by name, email, and phone.
- **SOS History Logs:** Users can view the history of all sent SOS signals with details on date/time, resolution status, and notification methods.
- **JWT-Based Authentication with Roles:** Secure API with roles `USER` and `CONTACT`, restricting access as appropriate.
- **Live Location Tracking:** After SOS is triggered, user’s live location streams in real-time to contacts over WebSocket.
- **Inactivity Auto-SOS:** Automatically triggers SOS if user is inactive for a set time (e.g., 15 minutes).
- **Service Discovery:** Eureka-based Discovery Server manages dynamic service registration and discovery.

---

## Architecture & Microservices Overview

| Microservice         | Responsibilities                                            | Key Technologies                                   |
|----------------------|-------------------------------------------------------------|---------------------------------------------------|
| **Discovery Server** (Eureka) | Service registry enabling microservice discovery and load balancing | Spring Cloud Netflix Eureka                        |
| **API Gateway**      | Entry point for all requests; JWT validation; routes traffic | Spring Cloud Gateway, JJWT                         |
| **MS-Auth**          | Authentication endpoints; JWT issuance and validation       | Spring Boot Web, JJWT                              |
| **MS-User**          | User profiles and trusted contacts CRUD operations          | Spring Boot Web, Spring Data JPA, PostgreSQL      |
| **MS-SOS**           | SOS alert triggering; real-time alert delivery via WebSocket; alert history; fallback email notifications | Spring Boot Web, WebSocket/STOMP, Redis, PostgreSQL, JavaMail |

---

## Technologies Used

- **Spring Boot** (Web, Data JPA, Security, WebSocket)
- **Spring Cloud Gateway** for API routing and security
- **Spring Cloud Netflix Eureka** for service discovery
- **JWT (JJWT)** for authentication and authorization
- **PostgreSQL** for persistent storage of users, contacts, and alert history
- **Redis** for temporary SOS session storage with TTL and quick access
- **WebSocket (STOMP)** for real-time push notifications to trusted contacts
- **JavaMail** for fallback email notifications
- **Lombok** for reducing boilerplate code
- **Gradle** for build and dependency management

---

## How It Works

1. **User triggers SOS** via `POST /api/sos/send` with location data.
2. The backend creates a new SOS alert, stores it in PostgreSQL, and stores an active session in Redis with a 3-minute TTL.
3. The alert is **broadcast in real-time** to trusted contacts over WebSocket.
4. Contacts receiving the alert can acknowledge or resolve it.
5. If no contact responds within 3 minutes (Redis TTL expiry), the system sends **fallback email notifications** to all trusted contacts.
6. User’s **live location updates** are streamed over WebSocket to contacts during an active SOS.
7. If the user is **inactive for 15 minutes**, the system automatically triggers an SOS using the last known location.

---

## Security

- All requests go through the **API Gateway** which validates JWT tokens.
- Services only accept requests with a valid **`X-Internal-Gateway`** header from the Gateway.
- Role-based access control ensures:
  - **USER** role manages own profile and contacts, triggers SOS.
  - **CONTACT** role can view and respond to SOS alerts sent to them.
- JWT tokens are issued by MS-Auth and validated centrally.

---

## Deployment

- Each microservice registers with **Eureka Discovery Server**.
- API Gateway routes traffic dynamically based on service registration.
- Redis used for caching active SOS sessions and acknowledgment states.
- PostgreSQL stores persistent user, contact, and alert data.
- WebSocket endpoints expose real-time alert streams per user.

---

## Getting Started

1. Start the **Discovery Server**.
2. Start **MS-User**, **MS-Auth**, **MS-SOS**, and **API Gateway** services.
3. Ensure Redis and PostgreSQL are running and configured in service properties.
4. Use API Gateway as the single entry point.
5. Authenticate users via MS-Auth to obtain JWT tokens.
6. Use tokens in headers to access protected endpoints.
