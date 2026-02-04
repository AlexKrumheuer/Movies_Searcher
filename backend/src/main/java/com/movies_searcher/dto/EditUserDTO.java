package com.movies_searcher.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EditUserDTO(
    @NotBlank(message = "Username cannot be blank")
    String username,
    @Email(message = "Invalid email format")
    String email,
    String currentPassword,
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*^#()\\[\\]{}|\\\\/+_.:;=~])[^\\s<>]{8,}$", message = "Password invalid")
    String newPassword,
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*^#()\\[\\]{}|\\\\/+_.:;=~])[^\\s<>]{8,}$", message = "Password invalid")
    String confirmNewPassword
) {
    
}
