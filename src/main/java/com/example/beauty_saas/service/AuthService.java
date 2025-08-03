package com.example.beauty_saas.service;

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
import lombok.extern.slf44j.Slf4j;
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
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       ParlourRepository parlourRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.parlourRepository = parlourRepository;
    }

    @Transactional
    public String registerAdmin(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Email is already registered!");
        }

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ADMIN"));

        Parlour parlour = null;
        if (registerRequest.getParlourId() != null) {
            parlour = parlourRepository.findById(registerRequest.getParlourId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parlour", "id", registerRequest.getParlourId()));
        } else {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Parlour ID is required for Admin registration.");
        }

        User user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(adminRole)
                .parlour(parlour)
                .build();

        userRepository.save(user);
        log.info("Admin user registered: {}", user.getEmail());
        return "Admin registered successfully.";
    }

    @Transactional
    public String registerCustomer(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Email is already registered!");
        }

        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "CUSTOMER"));

        User user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(customerRole)
                .build();

        userRepository.save(user);
        log.info("Customer user registered: {}", user.getEmail());
        return "Customer registered successfully.";
    }

    @Transactional
    public String registerSuperAdmin(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Email is already registered!");
        }

        Role superAdminRole = roleRepository.findByName("SUPERADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "SUPERADMIN"));

        User user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(superAdminRole)
                .build();

        userRepository.save(user);
        log.info("SuperAdmin user registered: {}", user.getEmail());
        return "SuperAdmin registered successfully.";
    }

    public JwtAuthResponse loginAdmin(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", loginRequest.getEmail()));

        if (!user.getRole().getName().equals("ADMIN")) {
            throw new BeautySaasApiException(HttpStatus.UNAUTHORIZED, "User is not an Admin.");
        }

        return new JwtAuthResponse(token, "Bearer", user.getRole().getName(), user.getEmail(), user.getName());
    }

    public JwtAuthResponse loginCustomer(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", loginRequest.getEmail()));

        if (!user.getRole().getName().equals("CUSTOMER")) {
            throw new BeautySaasApiException(HttpStatus.UNAUTHORIZED, "User is not a Customer.");
        }

        return new JwtAuthResponse(token, "Bearer", user.getRole().getName(), user.getEmail(), user.getName());
    }

    public JwtAuthResponse loginSuperAdmin(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", loginRequest.getEmail()));

        if (!user.getRole().getName().equals("SUPERADMIN")) {
            throw new BeautySaasApiException(HttpStatus.UNAUTHORIZED, "User is not a SuperAdmin.");
        }

        return new JwtAuthResponse(token, "Bearer", user.getRole().getName(), user.getEmail(), user.getName());
    }
}
