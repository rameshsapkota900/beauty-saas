package com.beautyparlour.service;

import com.beautyparlour.dto.request.LoginRequest;
import com.beautyparlour.dto.request.SuperAdminLoginRequest;
import com.beautyparlour.dto.response.LoginResponse;
import com.beautyparlour.entity.Admin;
import com.beautyparlour.repository.AdminRepository;
import com.beautyparlour.security.JwtTokenProvider;
import com.beautyparlour.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AdminRepository adminRepository;

    @Value("${superadmin.username}")
    private String superAdminUsername;

    @Value("${superadmin.password}")
    private String superAdminPassword;

    public LoginResponse authenticateAdmin(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String jwt = tokenProvider.generateToken(authentication, userPrincipal.getParlourId(), "ADMIN");

            return new LoginResponse(jwt, "ADMIN", userPrincipal.getName(), userPrincipal.getEmail());
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid credentials");
        }
    }

    public LoginResponse authenticateSuperAdmin(SuperAdminLoginRequest loginRequest) {
        if (!superAdminUsername.equals(loginRequest.getUsername()) || 
            !superAdminPassword.equals(loginRequest.getPassword())) {
            throw new RuntimeException("Invalid superadmin credentials");
        }

        String jwt = tokenProvider.generateSuperAdminToken(loginRequest.getUsername());
        return new LoginResponse(jwt, "SUPERADMIN", "Super Admin", loginRequest.getUsername());
    }
}
