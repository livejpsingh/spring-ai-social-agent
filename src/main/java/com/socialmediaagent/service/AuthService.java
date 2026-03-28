package com.socialmediaagent.service;

import com.socialmediaagent.domain.dto.AuthResponse;
import com.socialmediaagent.domain.dto.GoogleAuthRequest;
import com.socialmediaagent.domain.model.User;
import com.socialmediaagent.repository.UserRepository;
import com.socialmediaagent.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtDecoder jwtDecoder;

    @Autowired
    public AuthService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtDecoder = NimbusJwtDecoder.withJwkSetUri("https://www.googleapis.com/oauth2/v3/certs").build();
    }

    @Transactional
    public AuthResponse authenticateWithGoogle(GoogleAuthRequest request) {
        // 1. Decode and verify the Google ID token using Google's public keys
        Jwt jwt = jwtDecoder.decode(request.getCredential());
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");
        String picture = jwt.getClaimAsString("picture");

        // 2. Find or create user
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .fullName(name)
                    .passwordHash("") // No password for OAuth users
                    .pictureUrl(picture)
                    .isActive(true)
                    .build();
            return userRepository.save(newUser);
        });

        // 3. Generate our own backend JWT
        String token = jwtTokenProvider.generateToken(user.getEmail());

        // 4. Return the auth response
        return AuthResponse.builder()
                .token(token)
                .user(AuthResponse.UserData.builder()
                        .id(user.getId())
                        .name(user.getFullName())
                        .email(user.getEmail())
                        .picture(user.getPictureUrl())
                        .build())
                .build();
    }
}
