package org.saad.vetsphereai.controller;

import org.saad.vetsphereai.dto.AuthResponse;
import org.saad.vetsphereai.dto.LoginRequest;
import org.saad.vetsphereai.dto.RegisterRequest;
import org.saad.vetsphereai.entity.User;
import org.saad.vetsphereai.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @GetMapping("/profile")
    public String profile() {
        return "Welcome! you are authenticated.";
    }

    @GetMapping("/me")
    public User me(Authentication authentication) {
        return userService.getCurrentUser(authentication.getName());
    }
}