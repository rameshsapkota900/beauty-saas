package com.example.beautysaas.security;

public class UserPrincipal implements UserDetails {
import org.springframework.security.core.GrantedAuthority;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private UUID parlourId; // Null for SUPERADMIN

    public UserPrincipal(UUID id, String username, String password, Collection<? extends GrantedAuthority> authorities, UUID parlourId) {
import org.springframework.security.core.userdetails.UserDetails;
        this.username = username;
        this.password = password;
        this.authorities = authorities;

import java.util.Collections;

    public static UserPrincipal create(com.example.beautysaas.entity.User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));
        
import java.util.List;
import java.util.UUID;

public class UserPrincipal extends User implements UserDetails {
                authorities,
                user.getParlour() != null ? user.getParlour().getId() : null
        super(username, password, authorities);
        this.id = id;
        this.parlourId = parlourId;
        super(username, password, authorities);
    }
    }
                user.getId(),
                user.getEmail(),
                user.getPassword(),
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        );
    }

        return authorities;
        return id;
                user.getParlourId()

    public UUID getParlourId() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return username;
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public String getUsername() {
        return super.getUsername();
        return super.getPassword();

    @Override
    public boolean isAccountNonExpired() {
        return true;
        return super.getUsername();

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
