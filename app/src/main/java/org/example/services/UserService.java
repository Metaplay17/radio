package org.example.services;

import java.util.Optional;

import org.example.entities.User;
import org.example.exceptions.ConflictException;
import org.example.exceptions.IncorrectArgumentGivenException;
import org.example.exceptions.UserNotFoundException;
import org.example.repositories.UserRepository;
import org.example.requests.CreateUserRequest;
import org.example.security.SecurityUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(CreateUserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Передан null Request");
        }
        if (userRepository.existsByEmail(request.getUsername())) {
            throw new ConflictException("Email уже используется");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username уже используется");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPrivilegeLevel(request.getPrivilegeLevel());
        userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        if (username == null) {
            throw new IncorrectArgumentGivenException("Передан null username");
        }

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("Пользователь с username = " + username + " не найден"));
        
        return user;

    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("Пользователь с username = " + username + " не найден"));

        return new SecurityUser(user);

    }
}
