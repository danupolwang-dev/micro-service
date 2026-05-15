package com.example.user_service.service.Impl;

import com.example.user_service.model.Roles;
import com.example.user_service.repository.RolesRepository;
import com.example.user_service.service.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolesServiceImpl implements RolesService {

    @Autowired
    private RolesRepository rolesRepository;

    @Override
    public Roles saveRole(Roles role) {
        return rolesRepository.save(role);
    }

    @Override
    public Optional<Roles> getRoleById(Long id) {
        return rolesRepository.findById(id);
    }

    @Override
    public List<Roles> getAllRoles() {
        return rolesRepository.findAll();
    }

    @Override
    public void deleteRole(Long id) {
        rolesRepository.deleteById(id);
    }

    @Override
    public Roles updateRole(Long id, Roles roleDetails) {
        Roles role = rolesRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found with id " + id));
        role.setName(roleDetails.getName());
        role.setStatus(roleDetails.getStatus());
        // Set other fields to update as necessary
        return rolesRepository.save(role);
    }
}
