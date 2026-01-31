package com.movies_searcher.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.movies_searcher.dto.LoginResponseDTO;
import com.movies_searcher.dto.LoginUserDTO;
import com.movies_searcher.dto.RegisterUserDTO;
import com.movies_searcher.model.User;
import com.movies_searcher.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private AuthService authService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TokenService tokenService;
    @Mock
    private Authentication authentication;

    @Test
    @DisplayName("It must register a new user")
    void shouldRegisterNewUser() {
        User userMock = new User("Alex", "alex@gmail.com", "hashedPassword");
        RegisterUserDTO registerUserDTO = new RegisterUserDTO("Alex", "alex@gmail.com", "hashedpassword");
        when(passwordEncoder.encode(registerUserDTO.password())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(userMock);
        User registeredUser = authService.registerUser(registerUserDTO);
        assertEquals(registeredUser.getUsernameNotUserDetails(), "Alex");
        assertEquals(registeredUser.getEmail(), "alex@gmail.com");
        assertEquals(registeredUser.getPassword(), "hashedPassword");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("It must encode the password when registering a new user")
    void shouldEncodePasswordWhenRegisteringNewUser() {
        LoginUserDTO loginUserDTO = new LoginUserDTO("alex@gmail.com", "123");
        User userMock = new User("Alex","alex@gmail.com", "hashedPassword");
        String expectedToken = "valid.jwt.token";
        when(authentication.getPrincipal()).thenReturn(userMock);
        when(tokenService.generateToken(userMock)).thenReturn(expectedToken);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userMock);
        
        when(tokenService.generateToken(userMock)).thenReturn(expectedToken);

        LoginResponseDTO response = authService.loginUser(loginUserDTO);

        assertNotNull(response);
        assertEquals(expectedToken, response.token());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService).generateToken(userMock);
    }
}
 