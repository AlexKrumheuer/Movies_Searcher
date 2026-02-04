package com.movies_searcher.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.movies_searcher.repository.UserRepository;
import com.movies_searcher.service.TokenBlacklistService;
import com.movies_searcher.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    // autowired
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService;
    
    public SecurityFilter(TokenService tokenService, UserRepository userRepository, TokenBlacklistService tokenBlacklistService) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.tokenBlacklistService = tokenBlacklistService;
    }



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);

        if(token != null){
            if(tokenBlacklistService.isTokenBlacklisted(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            var email = tokenService.validateToken(token); 

            if(email != null && !email.isEmpty()){
                userRepository.findByEmail(email).ifPresent(user -> {
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
                
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null) return null;
        
        return authHeader.replace("Bearer ", "");
    }
}
