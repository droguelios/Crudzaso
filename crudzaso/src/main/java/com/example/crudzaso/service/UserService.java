package com.example.crudzaso.service;

import com.example.crudzaso.dto.ProfileUpdateRequest;
import com.example.crudzaso.entity.Users;

import java.util.List;

public interface UserService {
    Users registerUser(Users user);
    Users findByEmail(String email);
    Users findById(Long id);
    Users updateProfile(Long id, ProfileUpdateRequest req);
    void changePassword(Long id, String oldPassword, String newPassword);
    void requestPasswordReset(String email);
    void resetPassword(String token, String newPassword);
    List<Users> getAllUsers();
    List<Users> searchUsers(String query);
    void blockUser(Long id);
    void unblockUser(Long id);
    long countTotalUsers();
    long countActiveUsers();
    long countBlockedUsers();
}
