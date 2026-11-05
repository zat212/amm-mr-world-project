package org.myassignment.service;

import lombok.RequiredArgsConstructor;
import org.myassignment.entity.User;
import org.myassignment.exception.ResourceNotFoundException;
import org.myassignment.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * "Manage User Information". Sign up / login / Google login / OTP reset
 * stay in your existing auth code.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("User", id));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> ResourceNotFoundException.of("User", email));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateInformation(Long id, String name, String phone) {
        User user = getById(id);
        if (name != null && !name.isBlank()) {
            user.setFullName(name.trim());
        }
        if (phone != null) {
            user.setPhoneNumber(phone.trim());
        }
        return user;
    }
}
