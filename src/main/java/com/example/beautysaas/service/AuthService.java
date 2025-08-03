package com.example.beautysaas.service;

import com.example.beautysaas.config.SecurityConfig;
import com.example.beautysaas.dto.auth.JwtAuthResponse;
import com.example.beautysaas.dto.auth.LoginRequest;
import com.example.beautysaas.dto.auth.RegisterRequest;
import com.example.beautysaas.entity.Parlour;
import com.example.beautysaas.entity.Role;
import com.example.beautysaas.entity.User;
import com.example.beautysaas.exception.BeautySaasApiException;
import com.example.beautysaas.exception.ResourceNotFoundException;
import com.example.beautysaas.repository.ParlourRepository;
import com.example.beautysaas.repository.RoleRepository;
import com.example.beautysaas.repository.UserRepository;
import com.example.beautysaas.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ParlourRepository parlourRepository;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       SecurityConfig securityConfig, // Inject SecurityConfig to get passwordEncoder bean
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       ParlourRepository parlourRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = securityConfig.passwordEncoder(); // Get the PasswordEncoder bean
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.parlourRepository = parlourRepository;
    }

    @Transactional
    public void registerCustomer(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Email is already registered!");
        }

        User user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();

        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "CUSTOMER"));
        user.setRole(customerRole);

        userRepository.save(user);
        log.info("Customer registered successfully: {}", user.getEmail());
    }

    public JwtAuthResponse customerLogin(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", loginRequest.getEmail()));

        if (!user.getRole().getName().equals("CUSTOMER")) {
            throw new BeautySaasApiException(HttpStatus.UNAUTHORIZED, "Access Denied: Not a customer account.");
        }

        log.info("Customer {} logged in successfully.", user.getEmail());
        return new JwtAuthResponse(token, "Bearer", user.getRole().getName());
    }

    public JwtAuthResponse adminLogin(String parlourSlug, LoginRequest loginRequest) {
        Parlour parlour = parlourRepository.findBySlug(parlourSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Parlour", "slug", parlourSlug));

        User adminUser = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", loginRequest.getEmail()));

        if (!adminUser.getRole().getName().equals("ADMIN") || !adminUser.getParlour().getId().equals(parlour.getId())) {
            throw new BeautySaasApiException(HttpStatus.UNAUTHORIZED, "Access Denied: Invalid admin credentials or parlour association.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        log.info("Admin {} for parlour {} logged in successfully.", adminUser.getEmail(), parlourSlug);
        return new JwtAuthResponse(token, "Bearer", adminUser.getRole().getName());
    }
}
