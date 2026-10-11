# Student Fee Management System

## 1. Overview

The Student Fee Management System is a backend REST API application developed using Java and Spring Boot. It helps schools manage student information, record fee payments, track outstanding balances, and view payment history.

The application follows a layered architecture with separate Controller, Service, Repository, Entity, Request, and Response layers.

## 2. Technology Stack

- Language: Java 17
- Framework: Spring Boot
- Database: MySQL
- ORM: Spring Data JPA / Hibernate
- Build Tool: Maven
- API Style: RESTful APIs
- Validation: Jakarta Bean Validation
- Unit Testing: JUnit 5 and Mockito

## 3. Features

### Student Management
- Register a student with personal and academic details.
- Fetch student details by ID.
- Search students by name.
- Retrieve students using pagination.
- Soft-delete students by marking them inactive.

### Fee Management
- Record fee payments for registered students.
- Validate payments against the outstanding fee balance.
- Prevent duplicate transaction references.
- Retrieve a student's fee details.
- Calculate total fees, total paid, and outstanding balance.
- View payment history ordered by payment date.

### Error Handling and Validation
- Validate incoming request data.
- Return appropriate HTTP status codes.
- Handle application exceptions centrally.
- Apply transaction management to relevant service operations.

## 4. Prerequisites

Install the following before running the application:

- JDK 17
- Maven (or use the Maven wrapper if included)
- MySQL Server
- Eclipse IDE or another Java IDE
- Postman or a similar API client

## 5. Database Configuration

Create the database in MySQL:
CREATE DATABASE student_fee_management;


Configure your database connection in `src/main/resources/application.properties`.

Example:

properties
spring.datasource.url=jdbc:mysql://localhost:3306/student_fee_management
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true


Set the `DB_USERNAME` and `DB_PASSWORD` environment variables before starting the application.

Note: The configuration above is an example. Adjust it to match your actual database name, driver, and application configuration.

## 6. Run the Application

Clone the repository:
git clone https://github.com/YOUR-USERNAME/student-fee-management-system.git
cd student-fee-management-system

Build the project:
mvn clean install


Run the application:
mvn spring-boot:run


If the project is configured to package as a WAR or uses a different Spring Boot packaging setup, follow the corresponding configuration in `pom.xml`.

## 7. API Endpoints

Base URL: `http://localhost:8080`

### Student APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/students/register` | Register a student |
| GET | `/api/students/getById/{id}` | Fetch student details |
| GET | `/api/students/getAllStudents` | Search and paginate students |
| GET | `/api/students/fees/{id}` | Fetch student fee details |
| DELETE | `/api/students/delete/{id}` | Soft-delete a student |

For student search, pagination parameters include:

- `studentName` — optional search text
- `pageNumber` — zero-based page number
- `pageSize` — number of records per page, between 1 and 100

Example:

GET /api/students/getAllStudents?studentName=Aman&pageNumber=0&pageSize=10


### Fee Payment APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/payments/recordPayment` | Record a fee payment |
| GET | `/api/payments/getPaymentHistory/{studentId}` | Retrieve payment history |

## 8. Sample API Requests

### Register a Student

POST /api/students/register
Content-Type: application/json


{
  "firstName": "Aman",
  "lastName": "Mathur",
  "dateOfBirth": "2000-01-15",
  "gender": "MALE",
  "email": "aman@example.com",
  "phone": "9876543210",
  "fatherName": "Rajesh Mathur",
  "motherName": "Sunita Mathur",
  "address": "Sector 62",
  "city": "Noida",
  "state": "Uttar Pradesh",
  "postalCode": "201309",
  "className": "10",
  "section": "A",
  "totalFee": 10000
}


### Record a Fee Payment

POST /api/payments/recordPayment
Content-Type: application/json



{
  "studentId": 1,
  "amount": 3000.00,
  "paymentDate": "2026-10-10",
  "paymentMethod": "UPI",
  "transactionReference": "TXN-1001"
}


The student ID must refer to an existing active student. The payment must not exceed the outstanding balance.

### Fetch Student Fee Details

GET /api/students/fees/1


The response includes student details, total paid, outstanding balance, and payment history.

### Fetch Payment History


GET /api/payments/getPaymentHistory/1


## 9. Business Rules and Assumptions

1. Each student must have a unique email address.
2. A student must be active to receive a new fee payment.
3. A payment amount must be greater than zero.
4. A payment cannot exceed the student's outstanding fee balance.
5. Transaction references, when provided, must be unique.
6. Student deletion is implemented as a soft delete to preserve student and payment records.
7. Student searches return active students and support pagination.
8. Payment history is sorted by payment date in descending order.
9. Monetary calculations use `BigDecimal` to avoid floating-point precision errors.
10. Invalid requests and business-rule violations are handled through application exception handling.

## 10. Unit Testing

Unit tests are written using JUnit 5 and Mockito.

The test suite covers service and controller behavior, including:

- Successful student registration and payment recording.
- Student not found scenarios.
- Duplicate email and transaction reference validation.
- Outstanding balance validation.
- Student fee details and payment history.
- Pagination and student search.
- Soft deletion and inactive-student handling.

Run the tests with:
mvn test


## 11. Project Structure

src
├── main
│   ├── java
│   │   └── com.helloparent.fee_managment
│   │       ├── controller
│   │       ├── entity
│   │       ├── enums
│   │       ├── exceptions
│   │       ├── repository
│   │       ├── requests
│   │       ├── responses
│   │       ├── service
│   │       └── serviceImpl
│   └── resources
│       └── application.properties
└── test
    └── java
        └── com.helloparent.fee_managment
            ├── controller
            └── serviceImpl


## 12. Future Improvements

- Add authentication and role-based authorization.
- Add automated API documentation using OpenAPI/Swagger.
- Add integration tests using a dedicated test database.
- Add payment receipts and reporting.
- Add audit logging for student and payment operations.

## Author
Aman Mathur
