package com.movies_searcher.dto;

import java.time.LocalDate;

public record UserResponseDTO(
    String username,
    String email,
    LocalDate createdAt,
    String profileImageUrl,
    String bannerUrl
) {
    
}
