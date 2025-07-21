# MediaHub

MediaHub is a mini social media platform built with Spring Boot. It supports user registration, authentication (JWT), posting media, comments, likes, and friendships.

## Features
- User registration with unique email and phone number (passwords hashed with BCrypt)
- JWT-based authentication
- Upload, delete, and manage posts
- Add/remove friends
- Like, comment, and unlike posts

## Tech Stack
- Java 17+
- Spring Boot
- Spring Security (JWT)
- MySQL
- Lombok

## Setup Instructions
1. **Clone the repository:**
   ```bash
   git clone <your-repo-url>
   cd MediaHub
   ```
2. **Configure the database:**
   - Update `src/main/resources/application.properties` with your MySQL credentials.
   - Run the SQL scripts in `SQL Scripts/` to create the necessary tables.
3. **Build and run the project:**
   ```bash
   ./mvnw spring-boot:run
   ```
4. **API is available at:**
   - `http://localhost:8080/public/` (registration, login)
   - `http://localhost:8080/mediaHub/` (posts, friends, etc.)

## API Usage Examples
### Register a User
`POST /public/register`
```json
{
  "userName": "john",
  "email": "john@example.com",
  "phoneNumber": "1234567890",
  "password": "yourPassword",
  "role": "USER"
}
```
- **201 Created:** User registered
- **409 Conflict:** Duplicate email or phone

### Login
`POST /public/signIn`
```json
{
  "username": "john",
  "password": "yourPassword"
}
```
- **200 OK:** Returns JWT token and user info

### Upload a Post
`POST /mediaHub/uploadThePost`
- Form-data: `userId`, `caption`, `mediaType`, `file`

### Add a Friend
`POST /mediaHub/addFriend`
- Params: `userId_1`, `userId_2`

### Like a Post
`POST /mediaHub/like/{postId}`

## Project Structure
- `src/main/java/com/aithinkers/rest/` — Controllers
- `src/main/java/com/aithinkers/service/` — Business logic
- `src/main/java/com/aithinkers/repo/` — Repositories
- `src/main/java/com/aithinkers/entity/` — Entities
- `src/main/java/com/aithinkers/dto/` — DTOs

## Notes
- All passwords are securely hashed using BCrypt.
- JWT token is required for protected endpoints (see Swagger or Postman collection for details).
