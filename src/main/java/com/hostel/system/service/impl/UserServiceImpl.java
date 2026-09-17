package com.hostel.system.service.impl;

import com.hostel.system.factory.UserFactory;
import com.hostel.system.model.User;
import com.hostel.system.model.enums.Role;
import com.hostel.system.repository.UserRepository;
import com.hostel.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(Role role, String username, String password, String name, String email, String phone) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username '" + username + "' is already registered!");
        }

        // =========================================================================
        // GANG OF FOUR (GoF) DESIGN PATTERN: FACTORY METHOD PATTERN IN ACTION
        // Instead of calling new Student(), new Admin(), or new Warden(), we delegate
        // object instantiation to UserFactory.createUser().
        // =========================================================================
        String encodedPassword = passwordEncoder.encode(password);
        User user = UserFactory.createUser(role, username, encodedPassword, name, email, phone);

        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
