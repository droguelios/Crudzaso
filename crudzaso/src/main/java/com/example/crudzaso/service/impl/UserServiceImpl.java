package com.example.crudzaso.service.impl;

import com.example.crudzaso.dto.ProfileUpdateRequest;
import com.example.crudzaso.entity.Users;
import com.example.crudzaso.entity.enums.Rol;
import com.example.crudzaso.repository.UserRepository;
import com.example.crudzaso.service.EmailService;
import com.example.crudzaso.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public Users registerUser(Users user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRol(Rol.USER);
        user.setRegistrationDate(LocalDateTime.now());
        user.setUpdateDate(LocalDateTime.now());
        Users saved = userRepository.save(user);

        emailService.sendEmail(
                saved.getEmail(),
                "Welcome to CrudZaso DB Manager",
                "Hello " + saved.getName() + ",\n\n" +
                "Your account has been created successfully.\n\n" +
                "Welcome to CrudZaso DB Manager!"
        );
        return saved;
    }

    @Override
    public Users findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public Users findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public Users updateProfile(Long id, ProfileUpdateRequest req) {
        Users existing = findById(id);
        existing.setName(req.name());
        existing.setLastName(req.lastName());
        existing.setUpdateDate(LocalDateTime.now());
        return userRepository.save(existing);
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) {
        Users user = findById(id);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        emailService.sendEmail(
                user.getEmail(),
                "Password changed successfully",
                "Hello " + user.getName() + ",\n\n" +
                "Your password has been changed successfully."
        );
    }

    @Override
    public void requestPasswordReset(String email) {
        Users user = findByEmail(email);
        String token = UUID.randomUUID().toString();
        // In a real app, save token to DB with expiry and send reset link
        // For now, send it directly via email
        emailService.sendEmail(
                email,
                "Password reset request",
                "Hello " + user.getName() + ",\n\n" +
                "Use this token to reset your password: " + token + "\n\n" +
                "If you did not request this, please ignore this email."
        );
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        // In a real app, validate token from DB
        throw new RuntimeException("Password reset via token not yet implemented");
    }

    @Override
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<Users> searchUsers(String query) {
        return userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
    }

    @Override
    public void blockUser(Long id) {
        Users user = findById(id);
        user.setStatus("BLOCKED");
        user.setUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        emailService.sendEmail(
                user.getEmail(),
                "Account blocked",
                "Hello " + user.getName() + ",\n\n" +
                "Your account has been blocked. Please contact support."
        );
    }

    @Override
    public void unblockUser(Long id) {
        Users user = findById(id);
        user.setStatus("ACTIVE");
        user.setUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        emailService.sendEmail(
                user.getEmail(),
                "Account unblocked",
                "Hello " + user.getName() + ",\n\n" +
                "Your account has been unblocked. You can now log in."
        );
    }

    @Override
    public long countTotalUsers() {
        return userRepository.count();
    }

    @Override
    public long countActiveUsers() {
        return userRepository.countByStatus("ACTIVE");
    }

    @Override
    public long countBlockedUsers() {
        return userRepository.countByStatus("BLOCKED");
    }
}
