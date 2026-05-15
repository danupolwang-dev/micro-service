package com.example.user_service.service.Impl;

import com.example.user_service.model.Roles;
import com.example.user_service.model.User;
import com.example.user_service.model.UserRoles;
import com.example.user_service.repository.RolesRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.repository.UserRolesRepository;
import com.example.user_service.service.UserRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserRolesServiceImpl implements UserRolesService {

    @Autowired
    private UserRolesRepository userRolesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Override
    public UserRoles assignRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
        
        Roles role = rolesRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id " + roleId));

        UserRoles userRoles = new UserRoles(user, role);
        // You might want to set status and other audit fields here
        return userRolesRepository.save(userRoles);
    }

    @Override
    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        List<UserRoles> userRolesList = userRolesRepository.findByUser_Id(userId);
        Optional<UserRoles> userRoleToRemove = userRolesList.stream()
                .filter(ur -> ur.getRole().getId().equals(roleId))
                .findFirst();
        
        userRoleToRemove.ifPresent(userRolesRepository::delete);
    }

    @Override
    public List<UserRoles> getRolesByUserId(Long userId) {
        return userRolesRepository.findByUser_Id(userId);
    }
}
