# Viva Guide

## Singleton

**What is it?** A pattern that restricts a class to one shared instance.

**Why here?** Hostel-wide display configuration should have one consistent source.

**Classes:** `SystemConfigurationManager`.

**Flow:** `AdminController` calls `SystemConfigurationManager.getInstance()` and reads `getDefaultFeeCurrency()`.

**Say this:** "I used Singleton for shared immutable hostel configuration so every administrative view reads the same configuration instance."

## Factory Method

**What is it?** A creation method that hides product construction from the client.

**Why here?** User creation includes a username, password, and role.

**Classes:** `UserFactory`, `User`, `UserRole`.

**Flow:** `AdminController` -> `UserFactory.createUser()` -> `User` with `STUDENT` role -> `UserService`.

**Say this:** "The controller asks UserFactory for a role-specific user object instead of constructing the account directly."

## Builder

**What is it?** A pattern for constructing an object step by step.

**Why here?** Student records contain many required and optional fields.

**Classes:** `StudentBuilder`, `Student`.

**Flow:** builder setters -> `build()` -> `Student` -> `HostelManagementFacade.registerStudent()`.

**Say this:** "Builder keeps student construction readable and avoids a long constructor with many parameters."

## Facade

**What is it?** A simplified interface over multiple subsystems.

**Why here?** Hostel operations span student, room, complaint, and fee services.

**Classes:** `HostelManagementFacade`, `StudentService`, `RoomService`, `ComplaintService`, `FeeService`.

**Flow:** controller -> facade method -> one or more services -> repositories.

**Say this:** "The facade gives the controller one application-level API, reducing knowledge of individual subsystem coordination."

## Command

**What is it?** A pattern that encapsulates an operation as an object.

**Why here?** Allocation, vacating, complaint resolution, and fee generation are distinct hostel actions.

**Classes:** `Command`, `AllocateRoomCommand`, `VacateRoomCommand`, `ResolveComplaintCommand`, `GenerateFeeCommand`, `HostelCommandInvoker`, `HostelManagementReceiver`.

**Flow:** controller client -> concrete command -> invoker -> receiver -> service -> repository.

**Say this:** "The invoker executes a command without knowing how the receiver performs the hostel operation, and command history is available for auditing or future undo support."

## Security Questions

- Passwords are encoded with `BCryptPasswordEncoder`.
- Admin, warden, and student URLs require their matching role.
- CSRF is enabled for state-changing forms.
- Logout uses Spring Security's configured `/logout` route and invalidates the session.
- A cross-role request returns the controlled access-denied view.
