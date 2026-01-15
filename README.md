# --- Swimming Contest Manager

A desktop Manager system for managing Swimming Contest Participants.  
The application provides authentication, adding contests, search, and logout features.
It utilizes **JavaFX** for the client, **gRPC** for communication, **CompletableFuture** for asynchronous backend operations, and adheres to the **Observer pattern with Proxy** to broadcast live updates to all connected clients.

---

## --- Features

1. **Login**
   - The manager authenticates with username & password (stored as BCrypt hashes).
   - On success, a window opens showing all contests.

2. **Contests Creation**
   - A contest is created by adding it's features like distance, type, and assigning a participant.
   - All managers that are signed in can see live the changes made by other manager.
   - If a participant is already in a contest the manager can't create another entry for that same participant in the same contest.

3. **Search**
   - Search every context that a participant is singed for.
   - If multiple clients share the same name, all their contest are shown.
   - Results display customer name, contest, type, and the distance.

4. **Logout**
   - Ends the session and disconnects the user from the system.

---

## --- Tech Stack

- Java 17+
- JavaFX (desktop UI)
- gRPC + Protocol Buffers
- SQLite + JDBC
- BCrypt (password hashing)
- CompletableFuture (async backend)
- Observer & Proxy design patterns
- 
---

## 🔐 Security Notes

Passwords are never stored in plain text (only BCrypt hashes).

Password hashes are not returned in gRPC responses.

Sessions tracked to prevent duplicate logins.

---
