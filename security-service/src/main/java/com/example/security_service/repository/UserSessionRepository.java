package com.example.security_service.repository;

import com.example.security_service.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByUserName(String userName);
    Optional<UserSession> findByRefreshToken(String refreshToken);
    void deleteByUserName(String userName);
}
