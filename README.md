# Library Management System

A Spring Boot–based Library Management System that manages books, authors, borrowers, and borrowing transactions.  
Built with Java, Spring Boot, Spring Data JPA, and Gradle.

---

## Features

- **Author Management**: Create, update, list, and delete authors.
- **Book Management**: Create, update, list, and delete books. Check availability for borrowing.
- **Borrower Management**: Create, update, list, and delete borrowers.
- **Borrowing Transactions**: Record book borrowings and enforce availability rules.
- **Automatic Availability Tracking**: Books become unavailable when borrowed and are restored on return.
- **API Documentation**: Integrated Swagger UI for easy testing and exploration of endpoints.

---

## Entities

1. **Author**
   - `id`, `firstName`, `lastName`, `books`
   - Linked to `Book` (One-to-Many)

2. **Book**
   - `id`, `isbn`, `title`, `available`, `author`
   - Linked to `Author` (Many-to-One)
   - Linked to `BorrowingTransaction` (One-to-Many)

3. **Borrower**
   - `id`, `name`, `email`, `transactions`
   - Linked to `BorrowingTransaction` (One-to-Many)

4. **BorrowingTransaction**
   - `id`, `book`, `borrower`, `borrowDate`, `returnDate`, `status`
   - Status can be `BORROWED` or `RETURNED`

---

## Tech Stack

- **Java 17**
- **Spring Boot** (Web, Data JPA)
- **Hibernate** (ORM)
- **Gradle** (Build Tool)
- **PostgreSQL** (Database)
- **Swagger** (API documentation)

---

## Running the Project

1. **Clone the repository**
   ```bash
   git clone https://github.com/kaaek/Library-Management-System/tree/main
   cd library-management-system
2. **Build and run using Gradle**
   ```bash
   ./gradlew bootRun
3. **Access API Documentation**
   Swagger UI: `http://localhost:8080/swagger-ui/index.html`
