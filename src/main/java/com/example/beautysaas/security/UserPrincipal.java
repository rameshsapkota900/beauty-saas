package com.example.beautysaas.security;

import com.example.beautysaas.entity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class UserPrincipal extends User implements UserDetails {
    private UUID id;
    private UUID parlourId; // Null for SUPERADMIN

    public UserPrincipal(UUID id, String username, String password, Collection<? extends GrantedAuthority> authorities, UUID parlourId) {
        super(username, password, authorities);
        this.id = id;
        this.parlourId = parlourId;
    }

    public static UserPrincipal create(com.example.beautysaas.entity.User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities,
                user.getParlourId()
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
        return super.getAuthorities();
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public String getUsername() {
        return super.getUsername();
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
