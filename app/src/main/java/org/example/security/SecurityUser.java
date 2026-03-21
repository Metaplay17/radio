package org.example.security;

import java.util.List;

import org.example.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUser implements UserDetails {
    
    private final String username;
    private final String password;
    private final List<GrantedAuthority> authorities;

    public SecurityUser(String username, String password, List<GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
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

    public SecurityUser(User user) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getStringPrivilegeLevel().name());
        this.username = user.getUsername();
        this.password = user.getPasswordHash();
        this.authorities = List.of(authority);
    }
}
