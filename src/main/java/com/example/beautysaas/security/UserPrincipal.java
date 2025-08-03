package com.example.beautysaas.security;

import com.example.beautysaas.entity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class UserPrincipal implements UserDetails {
    private UUID id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private UUID parlourId; // Null for SUPERADMIN

    public UserPrincipal(UUID id, String username, String password, Collection<? extends GrantedAuthority> authorities, UUID parlourId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.parlourId = parlourId;
    }

    public static UserPrincipal create(com.example.beautysaas.entity.User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities,
                user.getParlour() != null ? user.getParlour().getId() : null
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getParlourId() {
        return parlourId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
