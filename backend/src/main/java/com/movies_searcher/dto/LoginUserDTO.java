package com.movies_searcher.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginUserDTO(
    @NotBlank(message="Email must not be blank")
    String email,
    @NotBlank(message="Password must not be blank")
    String password
) {
    
}
