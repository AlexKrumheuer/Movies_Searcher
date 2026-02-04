package com.movies_searcher.service;
import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.movies_searcher.dto.EditUserDTO;
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
    private final EmailChangeRequestService emailChangeRequestService;
    private final ImageService imageService;
    public AuthService(
        UserRepository userRepository, 
        PasswordEncoder passwordEncoder, 
        AuthenticationManager authenticationManager, 
        TokenService tokenService, 
        EmailChangeRequestService emailChangeRequestService,
        ImageService imageService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.emailChangeRequestService = emailChangeRequestService;
        this.imageService = imageService;
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

    public User editUser(User user, EditUserDTO dto) {
        if (dto.username() != null && !dto.username().isBlank()) {
            user.setName(dto.username());
        }

        if (dto.newPassword() != null && !dto.newPassword().isBlank()) {
            String actualPassword = user.getPassword();
            if (!passwordEncoder.matches(dto.currentPassword(), actualPassword)) {
                throw new IllegalArgumentException("Current password is incorrect");
            }
            String newPasswordHash = passwordEncoder.encode(dto.newPassword());
            user.setPassword(newPasswordHash);
        }

        if (dto.email() != null && !dto.email().isBlank() && dto.email().compareToIgnoreCase(user.getEmail()) != 0) {
            String actualPassword = user.getPassword();
            if (!passwordEncoder.matches(dto.currentPassword(), actualPassword)) {
                throw new IllegalArgumentException("Current password is incorrect");
            }
            emailChangeRequestService.createEmailChangeRequest(dto.email(), user);
        }

        return userRepository.save(user);
    }

    public User updateProfileImage(User user, MultipartFile file) {
        try {
            String imageUrl = imageService.uploadImage(file);
            user.setProfileImageUrl(imageUrl);
            return userRepository.save(user);
        } catch (IOException e) {
            System.err.println("Fail in Cloudinary: " + e.getMessage());
            
            throw new RuntimeException("Error proccessing image upload");
        }
    }

    public User updateBannerImage(User user, MultipartFile file) {
        try {
            String imageUrl = imageService.uploadImage(file);
            user.setBannerUrl(imageUrl);
            return userRepository.save(user);
        } catch (IOException e) {
            System.err.println("Fail in Cloudinary: " + e.getMessage());
            
            throw new RuntimeException("Error proccessing image upload");
        }
    }
}
