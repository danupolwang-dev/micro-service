package com.example.user_service.service;

import com.example.user_service.dto.UserAuthDTO;
import com.example.user_service.model.Roles;
import com.example.user_service.model.User;
import com.example.user_service.model.UserRoles;
import com.example.user_service.repository.RolesRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.repository.UserRolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private UserRolesRepository userRolesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUserName(username);
    }

    public Optional<UserAuthDTO> findUserForAuth(String username) {
        return userRepository.findByUserName(username)
                .map(UserAuthDTO::new);
    }

    @Transactional
    public User registerUser(User user) {
        if (userRepository.findByUserName(user.getUserName()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        Roles userRole = rolesRepository.findByName("USER")
                .orElseGet(() -> {
                    Roles newRole = new Roles();
                    newRole.setName("USER");
                    return rolesRepository.save(newRole);
                });

        UserRoles userRoles = new UserRoles(savedUser, userRole);
        userRolesRepository.save(userRoles);

        return savedUser;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserProfile() {
        return Optional.empty();
    }

    public User updateUser(User updatedUserData) {
        Optional<User> existingUserOptional = userRepository.findById(updatedUserData.getId());

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            existingUser.setUserName(updatedUserData.getUserName());
            existingUser.setPassword(updatedUserData.getPassword());
            existingUser.setSuspended(updatedUserData.isSuspended());
            return userRepository.save(existingUser);
        } else {
            return null;
        }
    }

    public User updateUserStatus(Long id, boolean isSuspended) {
        return userRepository.findById(id).map(user -> {
            user.setSuspended(isSuspended);
            return userRepository.save(user);
        }).orElse(null);
    }

    public long getRegisteredUserCount(String range) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime start = now;

        switch (range.toLowerCase()) {
            case "today":
                start = now.toLocalDate().atStartOfDay();
                break;
            case "week":
                start = now.minusWeeks(1);
                break;
            case "month":
                start = now.minusMonths(1);
                break;
            default:
                start = now.toLocalDate().atStartOfDay(); // Default to today
        }

        return userRepository.findAllByCreatedDtBetween(start, now).size();
    }

    public List<User> getRecentUsers() {
        return userRepository.findTop5ByOrderByCreatedDtDesc();
    }
}
