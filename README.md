# NETOLOGY JAVA DEVELOPER DIPLOMA PROJECT 
# "Cloud Storage Service"

## Overview

The Cloud Storage Service is a RESTful application that allows users to upload, download, list, and delete files. The service is secured with JWT-based authentication and integrates with a pre-prepared frontend application.

## Technologies Used

- Spring Boot
- Spring Security
- JWT (JSON Web Token)
- MongoDB
- Maven
- Docker

## Requirements

1. REST interface for integration with frontend.
2. Authentication for all requests.
3. Configuration through `application.yml`.
4. User and file information stored in MongoDB.

## Endpoints

### Authentication

- **POST /auth/login**
    - Description: User login.
    - Request Body:
      ```json
      {
        "username": "string",
        "password": "string"
      }
      ```
    - Response:
      ```json
      {
        "jwt": "string"
      }
      ```

### File Operations

- **GET /cloud/list**
    - Description: Get a list of uploaded files.
    - Headers: `Authorization: Bearer <token>`
    - Query Parameters: `userId=string`
    - Response:
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
    - Description: Upload a file.
    - Headers: `Authorization: Bearer <token>`
    - Query Parameters: `filename=string`
    - Request Body: Multipart form-data with file content.
    - Response:
      ```json
      {
        "id": "string",
        "filename": "string",
        "userId": "string",
        "size": "number"
      }
      ```

- **DELETE /cloud/file**
    - Description: Delete a file.
    - Headers: `Authorization: Bearer <token>`
    - Query Parameters: `filename=string`
    - Response: HTTP 200 OK

## Setup Instructions

### Prerequisites

- Java 17
- MongoDB
- Docker
- Maven

### Running Locally

1. **Clone the repository:**
   ```sh
   git clone https://github.com/emrahhakanagan/N-DiplomaProject-CloudStorageService.git
   cd N-DiplomaProject-CloudStorageService

2. **Configure MongoDB:**

Ensure MongoDB is running locally and update application.yml with the correct MongoDB URI if different.

3. **Build the project:**
```sh
mvn clean install
```

4. **Run the application:**
```sh
mvn spring-boot:run
```
5. **Frontend Setup:**
- Install Node.js (version 19.7.0 or higher).
- Navigate to the frontend directory.
- Install dependencies and start the frontend application:
```sh
npm install
npm run serve
```

6. **Configure Frontend:**
- Update the .env file in the frontend project root to point to your backend URL:
```sh
VUE_APP_BASE_URL=http://localhost:8080
```

### Docker Setup

1. **Build Docker image:**
```sh
docker build -t cloud-storage-service .
```

2. **Run Docker container:**
```sh
docker run -p 8080:8080 cloud-storage-service
```


### Security Configuration

The application uses JWT for authentication. The SecurityConfig class configures security settings:

- Disables CSRF.
- Allows unauthenticated access to /auth/login.
- Requires authentication for other endpoints.
- Configures JWT filter.