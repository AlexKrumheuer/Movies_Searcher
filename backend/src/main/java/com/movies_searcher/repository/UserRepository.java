package com.movies_searcher.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.movies_searcher.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}