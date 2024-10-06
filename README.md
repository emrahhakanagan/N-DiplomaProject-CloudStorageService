# NETOLOGY JAVA DEVELOPER DIPLOMA PROJECT 
# "Cloud Storage Service"

## Overview

This project is a cloud storage solution built with Spring Boot, MongoDB, Docker, and JWT-based security. The application provides a REST API for user authentication and file management.

## Table of Contents

- [Technologies Used](#technologies-used)
- [Prerequisites](#prerequisites)
- [API Endpoints](#api-endpoints)
    - [Authentication](#authentication)
    - [File Operations](#file-operations)
- [Setup Instructions](#setup-instructions)
    - [Running Locally](#running-locally)
    - [Frontend Setup](#frontend-setup)
    - [Environment Configuration](#environment-configuration)
    - [Running with Docker](#running-with-docker)
- [Testing](#testing)
- [Security Configuration](#security-configuration)
- [Notes](#notes)

## Technologies Used

- Java 17
- Spring Boot
- Spring Security (JWT-based)
- MongoDB
- Docker & Docker Compose
- Maven
- Node.js & npm (for frontend)

## Prerequisites

- Java 17
- MongoDB
- Docker
- Maven
- Node.js (version 19.7.0 or higher) & npm

## API Endpoints

### Authentication

- **POST /auth/login**
    - **Description:** User login.
    - **Request Body:**
      ```json
      {
        "username": "string",
        "password": "string"
      }
      ```
    - **Response:**
      ```json
      {
        "jwt": "string"
      }
      ```

- **POST /auth/register**
    - **Description:** Register a new user.
    - **Request Body:**
      ```json
      {
        "username": "string",
        "password": "string"
      }
      ```
    - **Response:**
      ```json
      {
        "id": "string",
        "username": "string"
      }
      ```

### File Operations

- **GET /cloud/list**
    - **Description:** Get a list of uploaded files.
    - **Headers:** `Authorization: Bearer <token>`
    - **Query Parameters:** `userId=string`
    - **Response:**
      ```json
      [
        {
          "id": "string",
          "filename": "string",
          "userId": "string",
          "size": "number"
        }
      ]
      ```

- **POST /cloud/file**
    - **Description:** Upload a file.
    - **Headers:** `Authorization: Bearer <token>`
    - **Query Parameters:** `filename=string`
    - **Request Body:** Multipart form-data with file content.
    - **Response:**
      ```json
      {
        "id": "string",
        "filename": "string",
        "userId": "string",
        "size": "number"
      }
      ```

- **DELETE /cloud/file**
    - **Description:** Delete a file.
    - **Headers:** `Authorization: Bearer <token>`
    - **Query Parameters:** `filename=string`
    - **Response:** HTTP 200 OK

## Setup Instructions

### Running Locally

1. **Clone the repository:**
```sh
   git clone https://github.com/your-repository-url.git
   cd your-repository-folder
```
2. **Configure MongoDB:**
   * Ensure MongoDB is running locally.
   * Update application.yml with the correct MongoDB URI if it's different from the default.

3. **Build the project:**
```sh
   mvn clean install
```

4. **Run the application:**
```sh
   mvn spring-boot:run
```

## Frontend Setup

1. **Navigate to the frontend directory.**
2. **Install dependencies and start the frontend application:**
```sh
   npm install
   npm run serve
```

## Environment Configuration
* In the root of the frontend project, create a .env file to point to your backend URL:
```env
   VUE_APP_BASE_URL=http://localhost:8080
```

## Running with Docker

1. **Build Docker image:**
```sh
   docker build -t cloud-storage-service .
```

2. **Run Docker container:**
```sh
   docker run -p 8080:8080 cloud-storage-service
```

3. **Using Docker Compose:**
* If you have a docker-compose.yml file in the root of the project, you can also use:
```sh
   docker-compose up --build
```


## Testing
* To run integration tests, execute:
```sh
   mvn clean test
```


## Security Configuration

The application uses JWT for authentication. The SecurityConfig class configures security settings:

* Disables CSRF.
* Allows unauthenticated access to /auth/login and /auth/register.
* Requires authentication for other endpoints.
* Configures JWT filter to validate tokens in each request.


## Notes
* Ensure ports 8080 (backend) and 3000 (frontend) are not occupied by other applications before starting the project.
* All API operations are secured and require a valid JWT token.
* To stop and remove Docker containers, use:
```sh
   docker-compose down
```