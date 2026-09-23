# Design Pattern Summary

| Pattern | Type | Main classes | Used for | Benefit |
| --- | --- | --- | --- | --- |
| Singleton | Creational | `SystemConfigurationManager` | Shared immutable hostel configuration | One consistent configuration instance |
| Factory Method | Creational | `UserFactory`, `User` | Role-aware user construction | Centralized user creation |
| Builder | Creational | `StudentBuilder`, `Student` | Multi-field student creation | Readable incremental construction |
| Facade | Structural | `HostelManagementFacade`, service subsystems | Student registration and room allocation | Lower controller coupling |
| Command | Behavioral | `Command`, concrete commands, `HostelCommandInvoker`, `HostelManagementReceiver` | Room, complaint, and fee actions | Encapsulated executable operations |

## Singleton

`SystemConfigurationManager` has a private constructor, a volatile `instance`, and a double-checked-locking `getInstance()` method. `AdminController` obtains the instance and uses its default fee currency for the dashboard. This keeps shared read-only configuration consistent across the application.

## Factory Method

`UserFactory.createUser(String username, String password, UserRole role)` creates the `User` product and applies the requested role. `AdminController` uses it when an administrator creates a student account. The factory is intentionally small because the current domain has one persisted `User` product with role data rather than separate persisted subclasses.

## Builder

`StudentBuilder` stores a `Student` product and exposes fluent setters for user, registration number, contact details, department, year, and parent information. `build()` returns the configured student. `AdminController.addStudent()` uses it before delegating persistence to the facade.

## Facade

`HostelManagementFacade` coordinates `StudentService`, `RoomService`, `ComplaintService`, and `FeeService`. Its `registerStudent`, `allocateRoom`, `vacateRoom`, `raiseComplaint`, `resolveComplaint`, and `generateFee` methods provide a compact application API. The admin controller uses the facade for student registration and allocation, avoiding direct coordination across those subsystems in that workflow.

## Command

`Command` defines `execute()`. `AllocateRoomCommand`, `VacateRoomCommand`, `ResolveComplaintCommand`, and `GenerateFeeCommand` hold the action data. `HostelCommandInvoker.submit()` records and executes commands, while `HostelManagementReceiver` delegates to the appropriate service. The resulting flow is controller client -> command -> invoker -> receiver -> service -> repository.
