package com.example.user_service.service;

import com.example.user_service.model.UserRoles;
import java.util.List;

public interface UserRolesService {
    UserRoles assignRoleToUser(Long userId, Long roleId);
    void removeRoleFromUser(Long userId, Long roleId);
    List<UserRoles> getRolesByUserId(Long userId);
}
