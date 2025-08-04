package com.example.beautysaas.config;

import com.example.beautysaas.security.CustomUserDetailsService;
import com.example.beautysaas.security.JwtAuthEntryPoint;
import com.example.beautysaas.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthEntryPoint authenticationEntryPoint;
    private final JwtAuthFilter authenticationFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthEntryPoint authenticationEntryPoint, JwtAuthFilter authenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .maximumSessions(3)
                        .maxSessionsPreventsLogin(false))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/admin-login/**").permitAll()
                        .requestMatchers("/superadmin-login/**").permitAll() // Added missing super admin endpoint
                        .requestMatchers("/categories/**").permitAll()
                        .requestMatchers("/services/**").permitAll()
                        .requestMatchers("/courses/**").permitAll()
                        .requestMatchers("/products/**").permitAll()
                        .requestMatchers("/certificates/**").permitAll()
                        .requestMatchers("/successful-students/**").permitAll()
                        .requestMatchers("/security/unlock-account/**").hasRole("SUPER_ADMIN") // Security admin endpoints
                        .requestMatchers("/security/audit-logs/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
