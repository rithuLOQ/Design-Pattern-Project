package com.hostel.system.model;

import com.hostel.system.model.enums.Role;
import jakarta.persistence.*;

/**
 * Concrete Product for Factory Method Pattern.
 * Target domain entity constructed cleanly via StudentBuilder (GoF Builder Pattern).
 */
@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "id")
public class Student extends User {

    @Column(name = "registration_number", nullable = false, unique = true, length = 50)
    private String registrationNumber;

    @Column(length = 100)
    private String department;

    private Integer year;

    @Column(length = 10)
    private String gender;

    @Column(length = 255)
    private String address;

    @Column(name = "parent_name", length = 100)
    private String parentName;

    @Column(name = "parent_phone", length = 20)
    private String parentPhone;

    @Column(name = "blood_group", length = 10)
    private String bloodGroup;

    @Column(name = "medical_condition", length = 255)
    private String medicalCondition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    public Student() {
        super();
        setRole(Role.ROLE_STUDENT);
    }

    public Student(String username, String password, String name, String email, String phone,
                   String registrationNumber, String department, Integer year, String gender,
                   String address, String parentName, String parentPhone, String bloodGroup,
                   String medicalCondition, Room room) {
        super(username, password, name, email, phone, Role.ROLE_STUDENT);
        this.registrationNumber = registrationNumber;
        this.department = department;
        this.year = year;
        this.gender = gender;
        this.address = address;
        this.parentName = parentName;
        this.parentPhone = parentPhone;
        this.bloodGroup = bloodGroup;
        this.medicalCondition = medicalCondition;
        this.room = room;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getMedicalCondition() {
        return medicalCondition;
    }

    public void setMedicalCondition(String medicalCondition) {
        this.medicalCondition = medicalCondition;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
