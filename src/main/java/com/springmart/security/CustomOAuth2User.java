package com.springmart.security;

import com.springmart.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oauth2User;
    private final User user;

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return user.getEmail();
    }

    public Long getUserId() {
        return user.getId();
    }

    public Long getId() {
        return user.getId();
    }

    public boolean hasRole(String roleName) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_" + roleName) || role.getName().equals(roleName));
    }

    public String getFirstName() {
        String fullName = user.getName();
        if (fullName != null && !fullName.isEmpty()) {
            return fullName.split(" ")[0];
        }
        return user.getEmail().split("@")[0];
    }

    public String getProfileImageUrl() {
        return user.getProfileImageUrl();
    }
}
