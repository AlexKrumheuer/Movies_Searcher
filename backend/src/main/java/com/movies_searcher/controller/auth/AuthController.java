package com.movies_searcher.controller.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movies_searcher.dto.LoginResponseDTO;
import com.movies_searcher.dto.LoginUserDTO;
import com.movies_searcher.dto.RegisterUserDTO;
import com.movies_searcher.model.User;
import com.movies_searcher.service.AuthService;
import com.movies_searcher.service.TokenBlacklistService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthController(AuthService authService, TokenBlacklistService tokenBlacklistService) {
        this.authService = authService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody @Valid RegisterUserDTO data) {
        try {
            User user = authService.registerUser(data);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
