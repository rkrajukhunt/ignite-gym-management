# 🏋️ Gym Management API

This project is a **Spring Boot** application with a **PostgreSQL database**, containerized using **Docker Compose**.

---

## 🚀 Prerequisites

Before running the project, ensure you have:

- **[Docker](https://www.docker.com/get-started)** (to run containers)
- **Docker Compose** (included with Docker)
- **[Maven](https://maven.apache.org/download.cgi)** (to build the Spring Boot JAR)

---

## 📥 Clone the Repository & Run the Application

```bash

📥 Clone the repository
   git clone https://github.com/rkrajukhunt/ignite-gym-management.git

📌 Navigate to the project directory
   cd ignite-gym-management

🧪 Run Test Cases
   mvn test

⚙️ Build the Spring Boot Application
   mvn clean package
   
▶️ Start the application using Docker Compose
   docker-compose -f docker-compose.yml up --build

🔍 Check Application Logs
   docker-compose logs -f gym-management-app

🛑 Stop the Containers
   docker-compose -f docker-compose.yml down

🌍 Health Check Endpoint
   http://localhost:8080/actuator/health

```

## 📌 Example API Endpoints
```bash

📝 Create a Class
Endpoint: POST /api/v1/classes

curl --location 'http://localhost:8080/api/v1/classes' \
--header 'Content-Type: application/json' \
--data '{
    "name": "Pilates",
    "startDate": "2025-03-10",
    "endDate": "2025-03-20",
    "startTime": "05:30",
    "duration": 40,
    "capacity": 12
}'

📝 Book a Class
Endpoint: POST /api/v1/bookings

curl --location 'http://localhost:8080/api/v1/bookings' \
--header 'Content-Type: application/json' \
--data '{
    "gymClassId": 2,
    "memberName": "Raju Khunt",
    "participationDate": "2025-03-15"
}'

🔍 Search for Bookings
Endpoint: GET /api/v1/bookings/search

curl --location 'http://localhost:8080/api/v1/bookings/search?memberName=Raju%20Khunt&startDate=2025-03-10&endDate=2025-03-20'
```
