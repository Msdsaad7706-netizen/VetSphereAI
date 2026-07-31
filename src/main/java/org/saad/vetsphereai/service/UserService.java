package org.saad.vetsphereai.service;

import org.saad.vetsphereai.dto.AuthResponse;
import org.saad.vetsphereai.dto.LoginRequest;
import org.saad.vetsphereai.dto.RegisterRequest;
import org.saad.vetsphereai.entity.User;
import org.saad.vetsphereai.repository.UserRepository;
import org.saad.vetsphereai.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User register(RegisterRequest request){
        User user =new User();
        user.setFullname(request.getFullName());
                      // password encrypt
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setEmail(request.getEmail());

        user.setRole(request.getRole());

            return userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));


        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    public User getCurrentUser(String email){
        return userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
    }



}
