package com.hostel.system.model;

import com.hostel.system.model.enums.Role;
import jakarta.persistence.*;

/**
 * Concrete Product for Factory Method Pattern.
 * Represents System Administrator with elevated permissions.
 */
@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "id")
public class Admin extends User {

    @Column(name = "department_code", length = 50)
    private String departmentCode;

    public Admin() {
        super();
        setRole(Role.ROLE_ADMIN);
    }

    public Admin(String username, String password, String name, String email, String phone, String departmentCode) {
        super(username, password, name, email, phone, Role.ROLE_ADMIN);
        this.departmentCode = departmentCode;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }
}
