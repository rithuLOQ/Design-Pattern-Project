# Smart Hostel Management System

An enterprise-level web application built with **Java 21**, **Spring Boot 3.x**, **Spring Data JPA**, **Spring Security**, and **Thymeleaf** to demonstrate the implementation of **Gang of Four (GoF) Design Patterns** for a University Design Patterns course.

---

## Table of Contents
1. [Project Overview](#project-overview)
2. [Tech Stack](#tech-stack)
3. [Gang of Four (GoF) Design Patterns](#gang-of-four-gof-design-patterns)
4. [Database Setup & MySQL Schema](#database-setup--mysql-schema)
5. [Setup & Execution Instructions](#setup--execution-instructions)
6. [Pre-seeded Demo User Accounts](#pre-seeded-demo-user-accounts)
7. [System Diagrams (PlantUML)](#system-diagrams-plantuml)
   - [UML Class Diagram](#1-uml-class-diagram)
   - [Use Case Diagram](#2-use-case-diagram)
   - [Sequence Diagram](#3-sequence-diagram)
   - [Entity Relationship (ER) Diagram](#4-entity-relationship-er-diagram)

---

## Project Overview

The **Smart Hostel Management System** automates key administrative and operational workflows in university residential halls, including:
- **Dashboard Analytics**: Real-time counter metrics for total/occupied/available rooms, student enrollment, pending complaints, and fee dues.
- **Student Management (CRUD)**: Complete profile tracking (Reg Number, Department, Year, Gender, Medical Conditions, Room Allocation).
- **Room Management (CRUD)**: Capacity tracking, floor layouts, room types (Single, Double, Triple, Deluxe), and automated status updates (Available, Full, Maintenance).
- **Complaint Management**: Student complaint submission and Admin/Warden resolution workflows.
- **Fee Management**: Hostel fee billing generation, payment gateway recording, and student fee payment history.

---

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.2.5, Spring MVC, Spring Data JPA, Hibernate, Spring Security 6, Maven
- **Frontend**: Thymeleaf, HTML5, CSS3, Bootstrap 5.3, Bootstrap Icons, JavaScript
- **Database**: MySQL 8.0+ (`hostel_db`)
- **IDE**: IntelliJ IDEA / Eclipse / VS Code

---

## Gang of Four (GoF) Design Patterns

### 1. Singleton Pattern (`DatabaseConnection.java`)
- **Package**: `com.hostel.system.singleton`
- **Intent**: Ensures that a class has only one instance in the entire JVM lifecycle and provides a thread-safe global access point.
- **Why Used Here**: Managing physical database metadata and connection parameters is resource-heavy. Double-checked locking with `volatile` guarantees that only a single instance coordinates system diagnostics across multi-threaded Spring HTTP requests.

### 2. Factory Method Pattern (`UserFactory.java`)
- **Package**: `com.hostel.system.factory`
- **Intent**: Defines an interface/class for creating an object, but lets subclasses or factory methods decide which class to instantiate.
- **Why Used Here**: `User` is an abstract base entity with concrete subclasses `Student`, `Admin`, and `Warden`. `UserFactory.createUser(Role, ...)` encapsulates instantiation logic, avoiding direct `new Student()`, `new Admin()`, `new Warden()` calls across services.

### 3. Builder Pattern (`StudentBuilder.java`)
- **Package**: `com.hostel.system.builder`
- **Intent**: Separates the construction of a complex object from its representation so that the same construction process can create different representations.
- **Why Used Here**: `Student` objects contain 14+ mandatory and optional attributes (Reg Number, Dept, Year, Address, Parent Phone, Medical Condition, Room). `StudentBuilder` provides fluent method chaining (`setName().setDepartment().setEmail().build()`), eliminating telescoping constructors and parameter order mistakes.

---

## Database Setup & MySQL Schema

The database script is located at: `sql/hostel_db.sql`.

### Tables Created:
1. `rooms` (Hostel room details and capacity counts)
2. `users` (Base user authentication table using Joined Inheritance)
3. `admins` (Extends `users` table)
4. `wardens` (Extends `users` table)
5. `students` (Extends `users` table with room FK)
6. `complaints` (Student maintenance tickets)
7. `fees` (Hostel billing and payment records)

---

## Setup & Execution Instructions

### Prerequisites
1. **Java Development Kit (JDK 21)** installed and set in `PATH`.
2. **Apache Maven 3.8+** installed.
3. **MySQL Server 8.0+** running locally on port `3306`.

### Step 1: Create Database & Seed Script
Open your MySQL Workbench or Command Line and execute:
```sql
SOURCE /path/to/project/sql/hostel_db.sql;
```
*(Alternatively, Spring Boot automatic schema update will create the database automatically on first startup).*

### Step 2: Configure Database Credentials
Open `src/main/resources/application.properties` and update your MySQL credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hostel_db?createDatabaseIfNotExist=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

### Step 3: Build & Run Application
Execute via terminal in project root directory:
```bash
mvn clean package
mvn spring-boot:run
```

Access the application in your browser at:  
👉 `http://localhost:8080`

---

## Pre-seeded Demo User Accounts

| Role | Username | Password | Access Level |
|---|---|---|---|
| **Admin** | `admin` | `admin123` | Full System Access (All Modules, User/Room/Fee CRUD) |
| **Warden** | `warden` | `warden123` | Student View, Room Status, Complaint Resolution, Fee Overview |
| **Student** | `student1` | `student123` | Personal Dashboard, File Complaints, Pay Hostel Fees |

---

## System Diagrams (PlantUML)

### 1. UML Class Diagram
```plantuml
@startuml ClassDiagram
skinparam classAttributeIconSize 0

' Abstract Superclass
abstract class User {
  - Long id
  - String username
  - String password
  - String name
  - String email
  - String phone
  - Role role
  - LocalDateTime createdAt
}

class Admin {
  - String departmentCode
}

class Warden {
  - String blockAssigned
}

class Student {
  - String registrationNumber
  - String department
  - Integer year
  - String gender
  - String address
  - String parentName
  - String parentPhone
  - String bloodGroup
  - String medicalCondition
}

class Room {
  - Long id
  - String roomNumber
  - Integer floor
  - Integer capacity
  - Integer occupiedCount
  - RoomType roomType
  - RoomStatus status
}

class Complaint {
  - Long id
  - String title
  - String description
  - ComplaintStatus status
  - LocalDateTime createdAt
  - LocalDateTime resolvedAt
}

class Fee {
  - Long id
  - BigDecimal amount
  - LocalDate dueDate
  - LocalDate paymentDate
  - FeeStatus status
  - String transactionRef
}

' GoF Design Pattern Classes
class DatabaseConnection << (S,#ADD1B2) Singleton >> {
  - {static} volatile DatabaseConnection instance
  - String dbUrl
  - boolean isConnected
  + {static} DatabaseConnection getInstance()
  + String getConnectionStatus()
}

class UserFactory << (F,#FFD700) Factory Method >> {
  + {static} User createUser(Role role, String username, String password, ...)
}

class StudentBuilder << (B,#90EE90) Builder >> {
  - String registrationNumber
  - String name
  - String department
  + StudentBuilder setRegistrationNumber(String)
  + StudentBuilder setName(String)
  + StudentBuilder setDepartment(String)
  + Student build()
}

' Inheritance
User <|-- Admin
User <|-- Warden
User <|-- Student

' Associations
Student "*" -- "0..1" Room : allocatedTo
Complaint "*" -- "1" Student : raisedBy
Fee "*" -- "1" Student : issuedTo

' Pattern Dependencies
UserFactory ..> User : creates
StudentBuilder ..> Student : builds
@enduml
```

---

### 2. Use Case Diagram
```plantuml
@startuml UseCaseDiagram
left to right direction
skinparam packageStyle rectangle

actor Admin as A
actor Warden as W
actor Student as S

rectangle "Smart Hostel Management System" {
  usecase "Authenticate & Login" as UC_Login
  usecase "View Role Dashboard" as UC_Dash
  
  usecase "Register & Manage Students" as UC_StudentCRUD
  usecase "Allocate / Vacate Room" as UC_Alloc
  usecase "Manage Hostel Rooms" as UC_RoomCRUD
  
  usecase "Raise Complaint" as UC_RaiseComp
  usecase "Resolve Complaints" as UC_ResComp
  
  usecase "Generate Hostel Fee" as UC_GenFee
  usecase "View & Pay Fee" as UC_PayFee
}

S --> UC_Login
S --> UC_Dash
S --> UC_RaiseComp
S --> UC_PayFee

W --> UC_Login
W --> UC_Dash
W --> UC_ResComp
W --> UC_Alloc
W --> UC_GenFee

A --> UC_Login
A --> UC_Dash
A --> UC_StudentCRUD
A --> UC_RoomCRUD
A --> UC_Alloc
A --> UC_ResComp
A --> UC_GenFee
@enduml
```

---

### 3. Sequence Diagram
*(Student Registration Flow via StudentBuilder Pattern)*

```plantuml
@startuml SequenceDiagram
autonumber
actor Admin
participant "StudentController" as Controller
participant "StudentServiceImpl" as Service
participant "StudentBuilder" as Builder
participant "StudentRepository" as StudentRepo
participant "RoomRepository" as RoomRepo

Admin -> Controller : POST /students/save (StudentRegistrationDto)
activate Controller

Controller -> Service : registerStudent(dto)
activate Service

Service -> RoomRepo : findById(dto.roomId)
activate RoomRepo
RoomRepo --> Service : Room entity
deactivate RoomRepo

Service -> Builder : new StudentBuilder()
activate Builder
Service -> Builder : setRegistrationNumber(dto.registrationNumber)
Service -> Builder : setName(dto.name)
Service -> Builder : setDepartment(dto.department)
Service -> Builder : setRoom(room)
Service -> Builder : build()
Builder --> Service : Student instance
deactivate Builder

Service -> StudentRepo : save(student)
activate StudentRepo
StudentRepo --> Service : Saved Student
deactivate StudentRepo

Service --> Controller : Registered Student Entity
deactivate Service

Controller --> Admin : Redirect /students with Toast Notification
deactivate Controller
@enduml
```

---

### 4. Entity Relationship (ER) Diagram
```plantuml
@startuml ERDiagram
entity "users" {
  * id : BIGINT <<PK>>
  --
  username : VARCHAR(50) <<UNIQUE>>
  password : VARCHAR(255)
  name : VARCHAR(100)
  email : VARCHAR(100) <<UNIQUE>>
  phone : VARCHAR(20)
  role : VARCHAR(20)
}

entity "students" {
  * id : BIGINT <<PK, FK>>
  --
  registration_number : VARCHAR(50) <<UNIQUE>>
  department : VARCHAR(100)
  year : INT
  gender : VARCHAR(10)
  room_id : BIGINT <<FK>>
}

entity "rooms" {
  * id : BIGINT <<PK>>
  --
  room_number : VARCHAR(20) <<UNIQUE>>
  floor : INT
  capacity : INT
  occupied_count : INT
  room_type : VARCHAR(20)
  status : VARCHAR(20)
}

entity "complaints" {
  * id : BIGINT <<PK>>
  --
  student_id : BIGINT <<FK>>
  title : VARCHAR(150)
  description : TEXT
  status : VARCHAR(20)
  created_at : DATETIME
}

entity "fees" {
  * id : BIGINT <<PK>>
  --
  student_id : BIGINT <<FK>>
  amount : DECIMAL(10,2)
  due_date : DATE
  payment_date : DATE
  status : VARCHAR(20)
}

users ||--o| students : "Joined Inheritance"
rooms ||--o{ students : "Allocated"
students ||--o{ complaints : "Raises"
students ||--o{ fees : "Owes/Pays"
@enduml
```
#   D e s i g n - P a t t e r n - P r o j e c t  
 