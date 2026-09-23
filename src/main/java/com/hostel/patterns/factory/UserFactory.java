package com.hostel.patterns.factory;

import com.hostel.entity.User;
import com.hostel.entity.UserRole;
import org.springframework.stereotype.Component;

/**
 * Factory Method pattern.
 *
 * Product: User
 * Concrete Products: Admin, Warden, Student user types
 * Factory: UserFactory creates the appropriate user instance.
 */
@Component
public class UserFactory {

    public User createUser(String username, String password, UserRole role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }
}
