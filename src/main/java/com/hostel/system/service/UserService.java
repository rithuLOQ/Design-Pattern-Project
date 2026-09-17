package com.hostel.system.service;

import com.hostel.system.model.User;
import com.hostel.system.model.enums.Role;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createUser(Role role, String username, String password, String name, String email, String phone);

    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);

    List<User> findAllUsers();

    boolean existsByUsername(String username);
}
