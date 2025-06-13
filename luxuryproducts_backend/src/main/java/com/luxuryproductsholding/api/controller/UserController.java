package com.luxuryproductsholding.api.controller;

import com.luxuryproductsholding.api.dao.UserDAO;
import com.luxuryproductsholding.api.models.CustomUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserDAO userDAO;

    public UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GetMapping
    public ResponseEntity<List<CustomUser>> getAllUsers() {
        return ResponseEntity.ok(userDAO.getAllUsers());
    }

    @GetMapping("/{email}")
    public ResponseEntity<CustomUser> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(this.userDAO.getUserByEmail(email));
    }

}
