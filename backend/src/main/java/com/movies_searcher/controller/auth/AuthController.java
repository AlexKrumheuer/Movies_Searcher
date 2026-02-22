package com.movies_searcher.controller.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.movies_searcher.dto.EditUserDTO;
import com.movies_searcher.dto.LoginResponseDTO;
import com.movies_searcher.dto.LoginUserDTO;
import com.movies_searcher.dto.RegisterUserDTO;
import com.movies_searcher.dto.UserResponseDTO;
import com.movies_searcher.model.User;
import com.movies_searcher.service.AuthService;
import com.movies_searcher.service.EmailChangeRequestService;
import com.movies_searcher.service.TokenBlacklistService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;



// Controller to handle authentication-related endpoints
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenBlacklistService tokenBlacklistService;
    private final EmailChangeRequestService emailChangeRequestService;

    public AuthController(AuthService authService, TokenBlacklistService tokenBlacklistService, EmailChangeRequestService emailChangeRequestService) {
        this.authService = authService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.emailChangeRequestService = emailChangeRequestService;
    }


    // Info about current authenticated user
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(new UserResponseDTO(user.getName(), user.getEmail(), user.getCreatedAt(), user.getProfileImageUrl(), user.getBannerUrl()));
    }

    // Confirm email update endpoint
    @GetMapping("/confirm-email")
    public ResponseEntity<UserResponseDTO> getMethodName(@RequestParam String token) {
        UserResponseDTO userReturn = emailChangeRequestService.confirmEmailUpdate(token);
        return ResponseEntity.ok(userReturn);
    }
    
    // Edit user profile endpoint
    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> editPerfil(
        @RequestBody @Valid EditUserDTO dto, 
        Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        User updatedUser = authService.editUser(user, dto);
        return ResponseEntity.ok(new UserResponseDTO(updatedUser.getName(), updatedUser.getEmail(), updatedUser.getCreatedAt(), updatedUser.getProfileImageUrl(), updatedUser.getBannerUrl()));
    }


    // Upload profile image endpoint
    @PostMapping("/me/profile-image")
    public ResponseEntity<UserResponseDTO> postProfileImage(@RequestParam("file") MultipartFile file, Authentication auth) {
        User user = (User) auth.getPrincipal();
        User updatedUser = authService.updateProfileImage(user, file);
        return ResponseEntity.ok(new UserResponseDTO(updatedUser.getName(), updatedUser.getEmail(), updatedUser.getCreatedAt(), updatedUser.getProfileImageUrl(), updatedUser.getBannerUrl()));
    }
    

    // Upload banner image endpoint
    @PostMapping("/me/banner-image")
    public ResponseEntity<UserResponseDTO> postBannerImage(@RequestParam("file") MultipartFile file, Authentication auth) {
        User user = (User) auth.getPrincipal();
        User updatedUser = authService.updateBannerImage(user, file);
        return ResponseEntity.ok(new UserResponseDTO(updatedUser.getName(), updatedUser.getEmail(), updatedUser.getCreatedAt(), updatedUser.getProfileImageUrl(), updatedUser.getBannerUrl()));
    }
    
    
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody @Valid RegisterUserDTO data) {
        try {
            User user = authService.registerUser(data);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody @Valid LoginUserDTO data) {
        try {
            LoginResponseDTO token = authService.loginUser(data);
            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Credentials");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String token = recoverToken(request);
        if (token != null) {
            tokenBlacklistService.addTokenToBlacklist(token);
        }
        return ResponseEntity.ok().build();
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null)
            return null;
        return authHeader.replace("Bearer ", "");
    }

}
