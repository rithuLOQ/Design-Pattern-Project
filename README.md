# Smart Hostel Management System V2

A Spring Boot 3 hostel operations application built as a clean V2 project. It uses Java 21, Spring MVC, Spring Security, Thymeleaf, Spring Data JPA, and H2 by default. MySQL can be selected with `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_DRIVER`, and `DB_DIALECT` environment variables.

## Run

```powershell
cd "d:\Rithul VIT\Design Patterns\Project\Design-Pattern-Project-V2"
mvn clean test
mvn spring-boot:run
```

Open `http://localhost:8080/login`. If the port is busy:

```powershell
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8083"
```

Demo accounts are seeded by `DataSeeder`:

| Role | Username | Password |
| --- | --- | --- |
| Admin | `admin` | `admin123` |
| Warden | `warden` | `warden123` |
| Student | `student` | `student123` |

The H2 database is in-memory by default and resets on restart. Passwords are stored using BCrypt.

## Design Patterns Used

### 1. Singleton

Actual class: `src/main/java/com/hostel/patterns/singleton/SystemConfigurationManager.java`

Where used: `AdminController` obtains `SystemConfigurationManager.getInstance()` and displays the default fee currency on the admin dashboard.

How it helps: one immutable source represents shared hostel configuration such as fee currency and default room type.

### 2. Factory Method

Actual class: `src/main/java/com/hostel/patterns/factory/UserFactory.java`

Where used: `AdminController.addStudent()` calls `userFactory.createUser(...)` before a student account is saved.

How it helps: user creation is kept behind one role-aware creation method instead of spreading construction details through controllers.

### 3. Builder

Actual class: `src/main/java/com/hostel/patterns/builder/StudentBuilder.java`

Where used: `AdminController.addStudent()` uses chained builder methods and `build()` to create the student aggregate.

How it helps: the student has many fields, so construction remains readable and avoids a long constructor call.

### 4. Facade

Actual class: `src/main/java/com/hostel/patterns/facade/HostelManagementFacade.java`

Where used: admin student registration and room allocation call `registerStudent()` and `allocateRoom()` on the facade.

How it helps: controllers use one simplified API while the facade coordinates StudentService, RoomService, ComplaintService, and FeeService.

### 5. Command

Actual classes: `Command`, `AllocateRoomCommand`, `VacateRoomCommand`, `ResolveComplaintCommand`, `GenerateFeeCommand`, `HostelCommandInvoker`, and `HostelManagementReceiver` under `src/main/java/com/hostel/patterns/command`.

Where used: admin and warden room, complaint, and fee actions create commands and submit them to `HostelCommandInvoker`.

How it helps: each hostel action is encapsulated as an object, so the invoker executes operations without knowing service details and can retain command history.

## Security and Quality Notes

- URL and method security isolate admin, warden, and student areas.
- CSRF protection is enabled for state-changing forms; logout is handled by Spring Security's configured `/logout` route.
- Invalid business actions render a controlled error page.
- Student dashboard queries are scoped to the authenticated student's ID.
- `mvn clean test` includes pattern unit tests; runtime role and authorization checks should also be exercised before a demonstration.
