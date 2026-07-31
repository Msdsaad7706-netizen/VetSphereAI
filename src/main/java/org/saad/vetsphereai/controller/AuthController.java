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

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @GetMapping("/profile")
    public String profile() {
        return "Welcome! you are authenticated.";
    }

    @GetMapping("/me")
    public User me(Authentication authentication){
        return userService.getCurrentUser(authentication.getName());
    }

    @RestController
    @RequestMapping("/api/admin")
    public class AdminController {
        @GetMapping("/dashboard")
        public String dashboard() {
            return "Welcome Admin";
        }
    }

    @RestController
    @RequestMapping("/api/veterinarian")
    public class DoctorController {

        @GetMapping("/dashboard")
        public String dashboard() {
            return "Welcome Doctor";
        }
    }

    @RestController
    @RequestMapping("/api/pet_owner")
    public class OwnerController {

        @GetMapping("/dashboard")
        public String dashboard() {
            return "Welcome Owner";
        }
    }



    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

}