package com.example.beautysaas.service;

import com.example.beautysaas.config.SecurityConfig;
import com.example.beautysaas.dto.user.UserProfileUpdateRequest;
import com.example.beautysaas.dto.user.UserDto;
import com.example.beautysaas.entity.User;
import com.example.beautysaas.exception.BeautySaasApiException;
import com.example.beautysaas.exception.ResourceNotFoundException;
import com.example.beautysaas.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, SecurityConfig securityConfig, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = securityConfig.passwordEncoder();
        this.modelMapper = modelMapper;
    }

    public UserDto getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return modelMapper.map(user, UserDto.class);
    }

    @Transactional
    public UserDto updateUserProfile(String email, UserProfileUpdateRequest updateRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        if (updateRequest.getName() != null) {
            user.setName(updateRequest.getName());
        }
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(updateRequest.getEmail())) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Email is already taken.");
            }
            user.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        log.info("User profile updated for: {}", updatedUser.getEmail());
        return modelMapper.map(updatedUser, UserDto.class);
    }
}
