package com.hostel.system.model;

import com.hostel.system.model.enums.Role;
import jakarta.persistence.*;

/**
 * Concrete Product for Factory Method Pattern.
 * Represents Hostel Warden managing specific hostel blocks.
 */
@Entity
@Table(name = "wardens")
@PrimaryKeyJoinColumn(name = "id")
public class Warden extends User {

    @Column(name = "block_assigned", length = 50)
    private String blockAssigned;

    public Warden() {
        super();
        setRole(Role.ROLE_WARDEN);
    }

    public Warden(String username, String password, String name, String email, String phone, String blockAssigned) {
        super(username, password, name, email, phone, Role.ROLE_WARDEN);
        this.blockAssigned = blockAssigned;
    }

    public String getBlockAssigned() {
        return blockAssigned;
    }

    public void setBlockAssigned(String blockAssigned) {
        this.blockAssigned = blockAssigned;
    }
}
