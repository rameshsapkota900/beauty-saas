package com.example.beautysaas.service;

import com.example.beautysaas.dto.auth.JwtAuthResponse;
import com.example.beautysaas.dto.auth.LoginRequest;
import com.example.beautysaas.dto.auth.PasswordChangeRequest;
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
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ParlourRepository parlourRepository;
    private final SecurityService securityService; // Added security service
    private final PasswordPolicyService passwordPolicyService; // Added password policy service

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       ParlourRepository parlourRepository,
                       SecurityService securityService,
                       PasswordPolicyService passwordPolicyService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.parlourRepository = parlourRepository;
        this.securityService = securityService;
        this.passwordPolicyService = passwordPolicyService;
    }

    @Transactional
    public void registerCustomer(RegisterRequest registerRequest, HttpServletRequest request) {
        // Validate password policy
        PasswordPolicyService.PasswordValidationResult validation =
                passwordPolicyService.validatePassword(registerRequest.getPassword());

        if (!validation.isValid()) {
            String ipAddress = getClientIpAddress(request);
            securityService.logSecurityEvent(registerRequest.getEmail(), "REGISTRATION_FAILED",
                    ipAddress, request.getHeader("User-Agent"),
                    "Password policy violation: " + String.join(", ", validation.getErrors()), false);
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST,
                    "Password does not meet policy requirements: " + String.join(", ", validation.getErrors()));
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            String ipAddress = getClientIpAddress(request);
            securityService.logSecurityEvent(registerRequest.getEmail(), "REGISTRATION_FAILED",
                    ipAddress, request.getHeader("User-Agent"), "Email already registered", false);
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Email is already registered!");
        }

        User user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();

        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_CUSTOMER"));
        user.setRole(customerRole);

        userRepository.save(user);

        // Log successful registration
        String ipAddress = getClientIpAddress(request);
        securityService.logSecurityEvent(registerRequest.getEmail(), "REGISTRATION_SUCCESS",
                ipAddress, request.getHeader("User-Agent"), "Customer registered successfully", true);

        log.info("Customer registered successfully: {}", user.getEmail());
    }

    public JwtAuthResponse customerLogin(LoginRequest loginRequest, HttpServletRequest request) {
        String email = loginRequest.getEmail();
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        // Check if account is locked
        if (securityService.isAccountLocked(email)) {
            securityService.logSecurityEvent(email, "LOGIN_BLOCKED", ipAddress, userAgent,
                "Login attempt on locked account", false);
            throw new BeautySaasApiException(HttpStatus.LOCKED,
                "Account is temporarily locked due to too many failed login attempts. Please try again later.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

            if (!user.getRole().getName().equals("ROLE_CUSTOMER")) {
                securityService.recordFailedLoginAttempt(email, ipAddress, userAgent);
                throw new BeautySaasApiException(HttpStatus.UNAUTHORIZED, "Access Denied: Not a customer account.");
            }

            // Generate session ID for audit logging
            String sessionId = UUID.randomUUID().toString();
            securityService.recordSuccessfulLogin(email, ipAddress, userAgent, sessionId);

            log.info("Customer {} logged in successfully.", user.getEmail());
            return new JwtAuthResponse(token, "Bearer", user.getRole().getName());

        } catch (BadCredentialsException e) {
            securityService.recordFailedLoginAttempt(email, ipAddress, userAgent);
            throw new BeautySaasApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
    }

    public JwtAuthResponse adminLogin(String parlourSlug, LoginRequest loginRequest, HttpServletRequest request) {
        String email = loginRequest.getEmail();
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        // Check if account is locked
        if (securityService.isAccountLocked(email)) {
            securityService.logSecurityEvent(email, "LOGIN_BLOCKED", ipAddress, userAgent,
                "Admin login attempt on locked account for parlour: " + parlourSlug, false);
            throw new BeautySaasApiException(HttpStatus.LOCKED,
                "Account is temporarily locked due to too many failed login attempts.");
        }

        try {
            Parlour parlour = parlourRepository.findBySlug(parlourSlug)
                    .orElseThrow(() -> new ResourceNotFoundException("Parlour", "slug", parlourSlug));

            User adminUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

            if (!adminUser.getRole().getName().equals("ROLE_ADMIN") ||
                !adminUser.getParlour().getId().equals(parlour.getId())) {
                securityService.recordFailedLoginAttempt(email, ipAddress, userAgent);
                throw new BeautySaasApiException(HttpStatus.UNAUTHORIZED,
                    "Access Denied: Invalid admin credentials or parlour association.");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);

            String sessionId = UUID.randomUUID().toString();
            securityService.recordSuccessfulLogin(email, ipAddress, userAgent, sessionId);

            log.info("Admin {} for parlour {} logged in successfully.", adminUser.getEmail(), parlourSlug);
            return new JwtAuthResponse(token, "Bearer", adminUser.getRole().getName());

        } catch (BadCredentialsException e) {
            securityService.recordFailedLoginAttempt(email, ipAddress, userAgent);
            throw new BeautySaasApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
    }

    /**
     * Authenticate a super admin user
     *
     * @param loginRequest The login credentials
     * @return JWT authentication response
     */
    public JwtAuthResponse superAdminLogin(LoginRequest loginRequest, HttpServletRequest request) {
        String email = loginRequest.getEmail();
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        log.info("Processing super admin login request for: {}", email);

        // Check if account is locked
        if (securityService.isAccountLocked(email)) {
            securityService.logSecurityEvent(email, "SUPERADMIN_LOGIN_BLOCKED", ipAddress, userAgent,
                "Super admin login attempt on locked account", false);
            throw new BeautySaasApiException(HttpStatus.LOCKED,
                "Account is temporarily locked due to too many failed login attempts.");
        }

        try {
            // First verify this is actually a super admin user
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

            // Standardized role naming convention check
            String roleName = user.getRole().getName();
            log.debug("Found user with role: {}", roleName);

            if (!roleName.equals("ROLE_SUPER_ADMIN")) {
                securityService.recordFailedLoginAttempt(email, ipAddress, userAgent);
                log.warn("Unauthorized super admin login attempt: {} has role {}", email, roleName);
                throw new BeautySaasApiException(HttpStatus.UNAUTHORIZED, "Access denied: Not a super admin account.");
            }

            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);

            String sessionId = UUID.randomUUID().toString();
            securityService.recordSuccessfulLogin(email, ipAddress, userAgent, sessionId);
            securityService.logSecurityEvent(email, "SUPERADMIN_LOGIN_SUCCESS", ipAddress, userAgent,
                "Super admin logged in successfully", true);

            log.info("Super admin login successful for: {}", email);
            return new JwtAuthResponse(token, "Bearer", user.getRole().getName());

        } catch (BadCredentialsException e) {
            securityService.recordFailedLoginAttempt(email, ipAddress, userAgent);
            log.error("Authentication failed for super admin: {}", e.getMessage());
            throw new BeautySaasApiException(HttpStatus.UNAUTHORIZED, "Invalid super admin credentials");
        }
    }

    /**
     * Change user password with validation
     */
    @Transactional
    public void changePassword(String email, PasswordChangeRequest passwordChangeRequest, HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        // Validate new password matches confirmation
        if (!passwordChangeRequest.getNewPassword().equals(passwordChangeRequest.getConfirmPassword())) {
            securityService.logSecurityEvent(email, "PASSWORD_CHANGE_FAILED", ipAddress, userAgent,
                    "Password confirmation mismatch", false);
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "New password and confirmation do not match");
        }

        // Validate new password policy
        PasswordPolicyService.PasswordValidationResult validation =
                passwordPolicyService.validatePassword(passwordChangeRequest.getNewPassword());

        if (!validation.isValid()) {
            securityService.logSecurityEvent(email, "PASSWORD_CHANGE_FAILED", ipAddress, userAgent,
                    "Password policy violation: " + String.join(", ", validation.getErrors()), false);
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST,
                    "New password does not meet policy requirements: " + String.join(", ", validation.getErrors()));
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        // Verify current password
        if (!passwordEncoder.matches(passwordChangeRequest.getCurrentPassword(), user.getPassword())) {
            securityService.recordFailedLoginAttempt(email, ipAddress, userAgent);
            throw new BeautySaasApiException(HttpStatus.UNAUTHORIZED, "Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));
        userRepository.save(user);

        // Log successful password change
        securityService.logSecurityEvent(email, "PASSWORD_CHANGED", ipAddress, userAgent,
                "Password changed successfully", true);

        log.info("Password changed successfully for user: {}", email);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
