package com.hostel.system.builder;

import com.hostel.system.model.Room;
import com.hostel.system.model.Student;
import com.hostel.system.model.enums.Role;

/**
 * =====================================================================================
 * GANG OF FOUR (GoF) DESIGN PATTERN: BUILDER PATTERN
 * =====================================================================================
 * 
 * PATTERN PURPOSE:
 * Separates the construction of a complex object from its representation, allowing the
 * exact same construction process to create different representations.
 * 
 * WHY BUILDER PATTERN IS USED FOR STUDENT:
 * 1. Solves Telescoping Constructor Problem: Student entities contain 14+ fields (Username,
 *    Password, Name, Email, Phone, RegNumber, Department, Year, Gender, Address, ParentName,
 *    ParentPhone, BloodGroup, MedicalCondition, Room). Constructors with 14 arguments are prone
 *    to parameter ordering bugs and unreadability.
 * 2. Step-by-Step Construction: Allows optional attributes (like medical condition, parent info)
 *    to be assigned fluidly via method chaining.
 * 3. Readability & Code Safety: Clear named setter methods yield self-documenting registration logic.
 * =====================================================================================
 */
public class StudentBuilder {

    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String registrationNumber;
    private String department;
    private Integer year;
    private String gender;
    private String address;
    private String parentName;
    private String parentPhone;
    private String bloodGroup;
    private String medicalCondition;
    private Room room;

    public StudentBuilder() {
    }

    public StudentBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public StudentBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public StudentBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public StudentBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public StudentBuilder setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public StudentBuilder setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
        return this;
    }

    public StudentBuilder setDepartment(String department) {
        this.department = department;
        return this;
    }

    public StudentBuilder setYear(Integer year) {
        this.year = year;
        return this;
    }

    public StudentBuilder setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public StudentBuilder setAddress(String address) {
        this.address = address;
        return this;
    }

    public StudentBuilder setParentName(String parentName) {
        this.parentName = parentName;
        return this;
    }

    public StudentBuilder setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
        return this;
    }

    public StudentBuilder setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
        return this;
    }

    public StudentBuilder setMedicalCondition(String medicalCondition) {
        this.medicalCondition = medicalCondition;
        return this;
    }

    public StudentBuilder setRoom(Room room) {
        this.room = room;
        return this;
    }

    /**
     * Builds and validates the Student instance.
     *
     * @return Fully initialized Student entity
     */
    public Student build() {
        Student student = new Student();
        student.setUsername(this.username != null ? this.username : this.registrationNumber);
        student.setPassword(this.password);
        student.setName(this.name);
        student.setEmail(this.email);
        student.setPhone(this.phone);
        student.setRegistrationNumber(this.registrationNumber);
        student.setDepartment(this.department);
        student.setYear(this.year);
        student.setGender(this.gender);
        student.setAddress(this.address);
        student.setParentName(this.parentName);
        student.setParentPhone(this.parentPhone);
        student.setBloodGroup(this.bloodGroup);
        student.setMedicalCondition(this.medicalCondition != null ? this.medicalCondition : "None");
        student.setRoom(this.room);
        student.setRole(Role.ROLE_STUDENT);
        return student;
    }
}
