package com.movies_searcher.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.movies_searcher.dto.UserResponseDTO;
import com.movies_searcher.model.EmailChangeRequest;
import com.movies_searcher.model.User;
import com.movies_searcher.repository.EmailChangeRequestRepository;
import com.movies_searcher.repository.UserRepository;

@Service
public class EmailChangeRequestService {
    private final EmailChangeRequestRepository emailChangeRequestRepository;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    public EmailChangeRequestService(
        EmailChangeRequestRepository emailChangeRequestRepository, 
        JavaMailSender mailSender, 
        UserRepository userRepository
    ) {
        this.emailChangeRequestRepository = emailChangeRequestRepository;
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    public void createEmailChangeRequest(String newEmail, User user) {
        EmailChangeRequest emailChangeRequest = new EmailChangeRequest(
            user,
            newEmail,
            UUID.randomUUID().toString()
        );
        emailChangeRequestRepository.save(emailChangeRequest);
        sendEmail(user.getUsername(), emailChangeRequest.getToken(), newEmail);
        sendSecurityAlert(user.getEmail(), newEmail);
    }

    public UserResponseDTO confirmEmailUpdate(String token) {
        Optional<EmailChangeRequest> optionalRequest = emailChangeRequestRepository.findByToken(token);
        if (optionalRequest.isPresent()) {
            EmailChangeRequest emailChangeRequest = optionalRequest.get();

            if(emailChangeRequest.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Token expired");
            }

            User user = emailChangeRequest.getUserId();
            user.setEmail(emailChangeRequest.getNewEmail());
            userRepository.save(user);
            UserResponseDTO userReturn = new UserResponseDTO(user.getName(), user.getEmail(), user.getCreatedAt(), user.getProfileImageUrl(), user.getBannerUrl());

            emailChangeRequestRepository.delete(emailChangeRequest);
            warnUpdatedEmail(user.getEmail());
            return userReturn;
        } else {
            throw new IllegalArgumentException("Invalid token");
        }
    }

    private void sendEmail(String to, String token, String newEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("CineScope - Confirme seu novo e-mail");
        String confirmationUrl = "http://localhost:5173/auth/confirm-email?token=" + token;
        message.setText("Olá! Você solicitou a alteração do seu e-mail para " + newEmail + ".\n\n" +
                        "Clique no link abaixo para confirmar:\n" + confirmationUrl +
                        "\n\nEste link expira em 2 horas.");
        mailSender.send(message);
    }

    private void sendSecurityAlert(String oldEmail, String newEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(oldEmail);
        message.setSubject("CineScope - Alerta de segurança");
        message.setText("Olá! Uma solicitação de alteração de e-mail para " + newEmail + " foi feita.\n" +
                        "Se foi você, apenas confirme no link enviado para o seu novo endereço.\n" +
                        "Se NÃO foi você, recomendamos trocar sua senha imediatamente.");
        mailSender.send(message);
    }

    private void warnUpdatedEmail(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("CineScope - E-mail alterado com sucesso");
        message.setText("Olá! Esse agora é seu e-mail para acessar sua conta do CineScope: " + ".\n");
        mailSender.send(message);
    }
}
