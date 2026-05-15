package com.example.product_service.repository;

import com.example.product_service.model.Components;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComponentsRepository extends JpaRepository<Components, Long> {
    java.util.List<Components> findByParent(Long parent);
    java.util.List<Components> findByCode(String code);

    @org.springframework.data.jpa.repository.Query("SELECT c FROM Components c WHERE c.parent IN (SELECT p.id FROM Components p WHERE p.code = :parentCode) AND c.status = 'ACTIVE'")
    java.util.List<Components> findByParentCode(@org.springframework.data.repository.query.Param("parentCode") String parentCode);
}
