package com.hostel.patterns;

import com.hostel.entity.Student;
import com.hostel.entity.User;
import com.hostel.entity.UserRole;
import com.hostel.patterns.builder.StudentBuilder;
import com.hostel.patterns.factory.UserFactory;
import com.hostel.patterns.singleton.SystemConfigurationManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class PatternImplementationTest {

    @Test
    void singletonReturnsTheSameConfigurationInstance() {
        assertSame(SystemConfigurationManager.getInstance(), SystemConfigurationManager.getInstance());
        assertEquals("INR", SystemConfigurationManager.getInstance().getDefaultFeeCurrency());
    }

    @Test
    void factoryCreatesAUserWithTheRequestedRole() {
        User user = new UserFactory().createUser("student", "secret", UserRole.STUDENT);

        assertEquals("student", user.getUsername());
        assertEquals(UserRole.STUDENT, user.getRole());
        assertEquals("secret", user.getPassword());
    }

    @Test
    void builderCreatesACompleteStudent() {
        User user = new UserFactory().createUser("student", "secret", UserRole.STUDENT);
        Student student = new StudentBuilder()
                .setUser(user)
                .setRegistrationNumber("V2-001")
                .setName("Test Student")
                .setEmail("test@example.com")
                .setPhone("9876543210")
                .setDepartment("Engineering")
                .setYear(2)
                .build();

        assertEquals("V2-001", student.getRegistrationNumber());
        assertEquals("Test Student", student.getName());
        assertEquals(2, student.getYear());
        assertSame(user, student.getUser());
    }
}
