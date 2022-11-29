package com.example.demo.controllers;

import java.util.Optional;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
@Log4j2
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        log.debug("Calling findById with id: {}", id);
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        log.debug("Calling findByUserName with username: {}", username);
        User user = userRepository.findByUsername(username);
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        log.debug("Calling createUser with body: {}", createUserRequest);
        String pw = createUserRequest.getPassword();
        if (Strings.isEmpty(pw) || pw.length() < 1) {
            log.error("Error - invalid password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        user.setPassword(encoder.encode(pw));
        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);
        userRepository.save(user);
        log.debug("Successfully created user");
        return ResponseEntity.ok(user);
    }

}
