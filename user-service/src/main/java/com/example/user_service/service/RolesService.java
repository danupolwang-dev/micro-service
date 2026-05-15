package com.example.user_service.service;

import com.example.user_service.model.Roles;
import java.util.List;
import java.util.Optional;

public interface RolesService {
    Roles saveRole(Roles role);
    Optional<Roles> getRoleById(Long id);
    List<Roles> getAllRoles();
    void deleteRole(Long id);
    Roles updateRole(Long id, Roles roleDetails);
}
