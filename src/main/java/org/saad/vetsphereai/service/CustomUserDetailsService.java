package org.saad.vetsphereai.service;

import org.saad.vetsphereai.entity.Role;
import org.saad.vetsphereai.entity.User;
import org.saad.vetsphereai.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
     private final UserRepository userRepository;

     public CustomUserDetailsService(UserRepository userRepository){
         this.userRepository = userRepository;
     }

     @Override
     public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
         User user = userRepository.findByEmail(email)
                 .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

         Role role = user.getRole() != null ? user.getRole() : Role.PET_OWNER;

         return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                 .password(user.getPassword())
                 .roles(role.name())
                 .build();
     }
}

