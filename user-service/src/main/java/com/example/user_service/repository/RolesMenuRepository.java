package com.example.user_service.repository;

import com.example.user_service.model.RolesMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RolesMenuRepository extends JpaRepository<RolesMenu, Long> {
    List<RolesMenu> findByRole_Id(Long roleId);

    @Transactional
    @Modifying
    @Query("DELETE FROM RolesMenu rm WHERE rm.role.id = ?1 AND rm.menu.id = ?2")
    void deleteByRoleIdAndMenuId(Long roleId, Long menuId);
}
