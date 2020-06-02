package com.skillbox.diploma.DiplomaSkillBox.main.security.jwt;

import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public final class JwtUserFactory {

    public JwtUserFactory() {
    }

    public static JwtUser create(User user) {
        return new JwtUser(
                user.getId(),
                user.getIsModerator(),
                user.getName(),
                user.getPassword(),
                user.getEmail(),
                user.getPhoto(),
                user.getRegTime(),
                user.getCode(),
                mapToGrantedAuthorities(user.getIsModerator()));
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(Boolean isModerator) {

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        if (isModerator) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return grantedAuthorities;
    }
}
