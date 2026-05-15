package com.example.user_service.repository;

import com.example.user_service.model.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRolesRepository extends JpaRepository<UserRoles, Long> {
    List<UserRoles> findByUser_Id(Long userId);
    List<UserRoles> findByRole_Id(Long roleId);
}
