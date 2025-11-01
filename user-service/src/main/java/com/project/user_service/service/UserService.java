package com.project.user_service.service;

import com.project.user_service.entity.User;
import com.project.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

//    @Autowired
//    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        // Check if user with the same phone or email exists
        Optional<User> existingUserByPhone = userRepository.findByPhone(user.getPhone());
        Optional<User> existingUserByEmail = userRepository.findByEmail(user.getEmail());

        if (existingUserByPhone.isPresent()) {
            throw new IllegalArgumentException("User with this phone number already exists.");
        }

        if (existingUserByEmail.isPresent()) {
            throw new IllegalArgumentException("User with this email address already exists.");
        }

        // Hash the password
//        String hashedPassword = passwordEncoder.encode(user.getPasswordHash());
//        user.setPasswordHash(hashedPassword);

        // Save the new user
        return userRepository.save(user);
    }
}
