package com.hostel.system.factory;

import com.hostel.system.model.Admin;
import com.hostel.system.model.Student;
import com.hostel.system.model.User;
import com.hostel.system.model.Warden;
import com.hostel.system.model.enums.Role;

/**
 * =====================================================================================
 * GANG OF FOUR (GoF) DESIGN PATTERN: FACTORY METHOD PATTERN
 * =====================================================================================
 * 
 * UML CLASS DIAGRAM STRUCTURE:
 * 
 *     +-----------------------+
 *     |     <<abstract>>      |
 *     |         User          |
 *     +-----------------------+
 *                 ^
 *                 |
 *       +---------+---------+
 *       |         |         |
 *   +-------+ +--------+ +---------+
 *   | Admin | | Warden | | Student |
 *   +-------+ +--------+ +---------+
 *       ^         ^         ^
 *       |         |         |
 *    [Instantiated Polymorphically]
 *                 ^
 *                 |
 *     +-----------------------+
 *     |      UserFactory      |
 *     |-----------------------|
 *     | + createUser(...)     |
 *     +-----------------------+
 * 
 * PATTERN PURPOSE:
 * Encapsulates the instantiation logic of User objects. Client applications interact with
 * the abstract superclass 'User' without needing to know concrete class implementations
 * or constructor variations for Admin, Warden, or Student.
 * 
 * WHY FACTORY METHOD IS USED HERE:
 * 1. Decoupling: Separates object creation logic from domain services and controllers.
 * 2. Extensibility: Adding a new user type (e.g., Staff or Visitor) requires updating only
 *    UserFactory without breaking existing user service calls.
 * 3. Enforces Uniform Role Assignment: Guarantees that generated users are initialized with correct
 *    security roles (ROLE_ADMIN, ROLE_WARDEN, ROLE_STUDENT).
 * =====================================================================================
 */
public class UserFactory {

    /**
     * Factory Method for creating specific User implementations based on Role type.
     *
     * @param role The target role enum (ROLE_ADMIN, ROLE_WARDEN, ROLE_STUDENT)
     * @param username Account username
     * @param password Account password (plaintext or encoded)
     * @param name Full display name
     * @param email Email address
     * @param phone Contact phone number
     * @return Concrete instance of User (Admin, Warden, or Student)
     */
    public static User createUser(Role role, String username, String password, String name, String email, String phone) {
        if (role == null) {
            throw new IllegalArgumentException("User Role cannot be null when creating a User instance!");
        }

        switch (role) {
            case ROLE_ADMIN:
                Admin admin = new Admin();
                admin.setUsername(username);
                admin.setPassword(password);
                admin.setName(name);
                admin.setEmail(email);
                admin.setPhone(phone);
                admin.setRole(Role.ROLE_ADMIN);
                admin.setDepartmentCode("ADMIN-GEN");
                return admin;

            case ROLE_WARDEN:
                Warden warden = new Warden();
                warden.setUsername(username);
                warden.setPassword(password);
                warden.setName(name);
                warden.setEmail(email);
                warden.setPhone(phone);
                warden.setRole(Role.ROLE_WARDEN);
                warden.setBlockAssigned("General Hostels");
                return warden;

            case ROLE_STUDENT:
                Student student = new Student();
                student.setUsername(username);
                student.setPassword(password);
                student.setName(name);
                student.setEmail(email);
                student.setPhone(phone);
                student.setRole(Role.ROLE_STUDENT);
                return student;

            default:
                throw new IllegalArgumentException("Unsupported User Role: " + role);
        }
    }
}
