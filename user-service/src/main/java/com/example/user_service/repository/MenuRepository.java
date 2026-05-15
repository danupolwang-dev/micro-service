package com.example.user_service.repository;

import com.example.user_service.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    @Query("SELECT DISTINCT m FROM Menu m JOIN m.rolesMenus rm JOIN rm.role r " +
            "WHERE r.name IN :roleNames AND m.status = 'ACTIVE' AND rm.status = 'ACTIVE' " +
            "ORDER BY m.displayOrder ASC")
    List<Menu> findMenusByRoleNames(@Param("roleNames") List<String> roleNames);
}
