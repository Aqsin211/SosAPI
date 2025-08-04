SilentSignals - Emergency Alert System Overview SilentSignals is a secure, real-time panic alert system that enables users to silently trigger SOS signals during emergencies. The system delivers instant notifications to trusted contacts via WebSocket, with fallback to SMS/Email when recipients are offline.

Technical Architecture Microservices API Gateway: Spring Cloud Gateway (Port 8080)

MS-Auth: JWT Authentication (Port 8081)

MS-User: User/Contact Management (Port 8082)

MS-SOS: Alert Processing (Port 8083)

Discovery Server: Eureka (Port 8761)

Core Technologies Backend: Spring Boot 3.5.4, Java 21

Database: PostgreSQL (persistent), Redis (ephemeral)

Communication: WebSocket/STOMP, REST APIs

Security: JWT, Spring Security

Notifications: JavaMail

Key Features Alert System Silent SOS triggering via POST /sos/trigger

Real-time WebSocket alerts to /topic/sos-alerts/{userId}

3-minute escalation to SMS/Email if unacknowledged

User Management CRUD operations for trusted contacts

Role-based access (USER/CONTACT)

Safety Mechanisms 15-minute inactivity auto-SOS

Location tracking with 20-minute TTL

End-to-end JWT authentication
