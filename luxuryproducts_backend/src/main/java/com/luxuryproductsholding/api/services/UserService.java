package com.luxuryproductsholding.api.services;

import com.luxuryproductsholding.api.dao.UserRepository;
import com.luxuryproductsholding.api.models.CustomUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        CustomUser customUser = userRepository.findByEmail(email);
        if (customUser == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return new org.springframework.security.core.userdetails.User(
                customUser.getEmail(),
                customUser.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + customUser.getRole()))
        );
    }

    public CustomUser getCustomUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}