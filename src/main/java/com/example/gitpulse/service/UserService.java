
package com.example.gitpulse.service;

import com.example.gitpulse.model.User;
import com.example.gitpulse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GitHubService gitHubService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    public User fetchAndSaveGitHubUser(String username) {
        try {
            User user = gitHubService.fetchGitHubUser(username);
            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch GitHub user: " + username, e);
        }
    }
}
