package com.springmart.security;

import com.springmart.entity.Role;
import com.springmart.entity.User;
import com.springmart.repository.RoleRepository;
import com.springmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String oauth2Id = oauth2User.getAttribute("id") != null ? oauth2User.getAttribute("id").toString()
                : oauth2User.getAttribute("sub");
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String profileImage = oauth2User.getAttribute("picture") != null ? oauth2User.getAttribute("picture")
                : oauth2User.getAttribute("avatar_url");

        // Find or create user
        Optional<User> existingUser = userRepository.findByOauth2ProviderAndOauth2Id(provider, oauth2Id);

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            // Update user info
            user.setName(name);
            user.setProfileImageUrl(profileImage);
        } else {
            // Create new user
            user = User.builder()
                    .email(email)
                    .name(name)
                    .oauth2Provider(provider)
                    .oauth2Id(oauth2Id)
                    .profileImageUrl(profileImage)
                    .enabled(true)
                    .build();

            // Assign default role
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            user.addRole(userRole);
        }

        userRepository.save(user);

        return new CustomOAuth2User(oauth2User, user);
    }
}
