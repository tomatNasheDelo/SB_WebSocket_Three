# Third Spring Boot WebSocket chat learning repo

This project is the next step after the basic room chat:
- user registration / login
- JWT-based authentication
- JSON REST auth endpoints
- rooms with owner information
- room history with pagination-ready service logic
- WebSocket/STOMP messages by room

This is a learning-only project, not a production-ready chat system.

## Run

```bash
./gradlew bootRun
```

Open http://localhost:8080/

Then:
1. Register a user
2. Login
3. Create or join a room
4. Send messages in the room

The backend uses an in-memory H2 database for quick local development.

## Notes

- `SecurityConfig` allows `/api/auth/**`, `/ws/**`, `/index.html`
- WebSocket still uses a basic Spring STOMP setup without advanced authorization rules
- This is the point before introducing Redis/RabbitMQ and production-level room permissions
