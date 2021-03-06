package com.skillbox.diploma.DiplomaSkillBox.main.security.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;

public class JwtUser implements UserDetails {

    private final Long id;
    private final Boolean isModerator;
    private final String username;
    private final String password;
    private final String email;
    private final String photo;
    private final Instant regTime;
    private final String code;
    private final Collection<? extends GrantedAuthority> authorities;

    public JwtUser(
            Long id,
            Boolean isModerator,
            String username,
            String password,
            String email,
            String photo,
            Instant regTime,
            String code,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.id = id;
        this.isModerator = isModerator;
        this.username = username;
        this.password = password;
        this.email = email;
        this.photo = photo;
        this.regTime = regTime;
        this.code = code;
        this.authorities = authorities;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Boolean getIsModerator() {
        return isModerator;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoto() {
        return photo;
    }

    @JsonIgnore
    public Instant getRegTime() {
        return regTime;
    }

    public String getCode() {
        return code;
    }
}
