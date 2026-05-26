package org.example.services;

import org.example.aspects.NotNullArg;
import org.example.controllers.requests.CreateUserRequest;
import org.example.entities.User;
import org.example.exceptions.ConflictException;
import org.example.exceptions.UserNotFoundException;
import org.example.repositories.UserRepository;
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

    @NotNullArg
    public void createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email уже используется");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username уже используется");
        }

        User user = new User(request.getUsername(), passwordEncoder.encode(request.getPassword()), request.getEmail(), request.getPrivilegeLevel());
        userRepository.save(user);
    }

    @NotNullArg
    public User getUserByUsername(String username) {

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
