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

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
     
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody @Valid RegisterUserDTO data) {
        try {
            User user = authService.registerUser(data);
            return ResponseEntity.ok(user);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody @Valid LoginUserDTO data) {
        try {
            LoginResponseDTO token = authService.loginUser(data);
            return ResponseEntity.ok(token);
        } catch(AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Credentials");
        }
    }
    
}
