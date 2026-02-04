package com.movies_searcher.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.movies_searcher.model.EmailChangeRequest;

public interface EmailChangeRequestRepository extends JpaRepository<EmailChangeRequest, Long> {
    Optional<EmailChangeRequest> findByToken(String token);
}
