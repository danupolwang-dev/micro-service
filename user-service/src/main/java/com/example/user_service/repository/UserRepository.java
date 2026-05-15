package com.example.user_service.repository;

import com.example.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String userName); // Corrected method name

    /**
     * Finds all users who registered within a given date range.
     * @param start The start of the date range.
     * @param end The end of the date range.
     * @return A list of users.
     */
    List<User> findAllByCreatedDtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Finds the top 5 most recently registered users.
     * @return A list of the 5 newest users.
     */
    List<User> findTop5ByOrderByCreatedDtDesc();
}
