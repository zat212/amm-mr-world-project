package org.myassignment.controller;

import lombok.RequiredArgsConstructor;
import org.myassignment.entity.User;
import org.myassignment.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Finds the logged-in user.
 * ASSUMPTION: authentication.getName() is the user's email (the JWT subject).
 * If your JWT stores the user id instead, change get() to userService.getById(Long.valueOf(...)).
 */
@Component
@RequiredArgsConstructor
public class CurrentUserResolver {

    private final UserService userService;

    public User get(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please log in.");
        }
        return userService.getByEmail(authentication.getName());
    }
}
