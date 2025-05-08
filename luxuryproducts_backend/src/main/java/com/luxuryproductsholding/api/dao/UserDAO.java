package com.luxuryproductsholding.api.dao;

import com.luxuryproductsholding.api.models.CustomUser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDAO {

    private  final UserRepository userRepository;


    public UserDAO(UserRepository userRepository) {
        this.userRepository = userRepository;

    }
    public List<CustomUser> getAllUsers() {
        return this.userRepository.findAll();
    }

    public CustomUser getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

}
