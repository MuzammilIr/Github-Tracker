package com.example.github_tracker.service;

import com.example.github_tracker.dto.AuthResponse;
import com.example.github_tracker.model.User;
import com.example.github_tracker.repository.UserRepository;
import com.example.github_tracker.security.JwtTokenProvider;
import com.example.github_tracker.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public AuthResponse authenticateUser(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        return new AuthResponse(
                jwt,
                "Bearer",
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                userPrincipal.getAuthorities().iterator().next().getAuthority()
        );
    }

    public User updateUserFromGithub(String githubId, String username, String email) {
        User user = userRepository.findByGithubId(githubId)
                .orElse(new User());

        user.setGithubId(githubId);
        user.setUsername(username);
        user.setEmail(email);

        // For new users, set role to USER
        if (user.getId() == null) {
            user.setRole(User.Role.USER);
        }

        return userRepository.save(user);
    }
}