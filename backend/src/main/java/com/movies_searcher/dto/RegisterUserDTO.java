package com.movies_searcher.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterUserDTO(
    @NotBlank(message="Username must not be blank")
    String username,
    @Email(message="Email must not be blank")
    String email,
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*^#()\\[\\]{}|\\\\/+_.:;=~])[^\\s<>]{8,}$", message = "Password invalid")
    String password
) {
    
}
