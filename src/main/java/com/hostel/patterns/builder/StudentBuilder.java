package com.hostel.patterns.builder;

import com.hostel.entity.Student;
import com.hostel.entity.User;

/**
 * Builder pattern.
 *
 * Builder: StudentBuilder encapsulates the construction process.
 * Product: Student
 * Why useful: handles many optional fields cleanly during student creation.
 */
public class StudentBuilder {
    private final Student student = new Student();

    public StudentBuilder setUser(User user) {
        student.setUser(user);
        return this;
    }

    public StudentBuilder setRegistrationNumber(String registrationNumber) {
        student.setRegistrationNumber(registrationNumber);
        return this;
    }

    public StudentBuilder setName(String name) {
        student.setName(name);
        return this;
    }

    public StudentBuilder setEmail(String email) {
        student.setEmail(email);
        return this;
    }

    public StudentBuilder setPhone(String phone) {
        student.setPhone(phone);
        return this;
    }

    public StudentBuilder setDepartment(String department) {
        student.setDepartment(department);
        return this;
    }

    public StudentBuilder setYear(Integer year) {
        student.setYear(year);
        return this;
    }

    public StudentBuilder setAddress(String address) {
        student.setAddress(address);
        return this;
    }

    public StudentBuilder setParentName(String parentName) {
        student.setParentName(parentName);
        return this;
    }

    public StudentBuilder setParentPhone(String parentPhone) {
        student.setParentPhone(parentPhone);
        return this;
    }

    public Student build() {
        return student;
    }
}
