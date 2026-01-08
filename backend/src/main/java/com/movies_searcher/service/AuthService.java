package com.movies_searcher.service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.movies_searcher.dto.LoginResponseDTO;
import com.movies_searcher.dto.LoginUserDTO;
import com.movies_searcher.dto.RegisterUserDTO;
import com.movies_searcher.model.User;
import com.movies_searcher.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public User registerUser(RegisterUserDTO data) {
        String passwordHash = passwordEncoder.encode(data.password());
        User entity = new User(
            data.username(),
            data.email(),
            passwordHash
        );

        return userRepository.save(entity);
    }

    public LoginResponseDTO loginUser(LoginUserDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((User) auth.getPrincipal());
        return new LoginResponseDTO(token);
    }
}
