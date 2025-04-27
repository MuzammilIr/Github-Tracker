
package com.example.gitpulse.controller;

import com.example.gitpulse.model.Repository;
import com.example.gitpulse.model.User;
import com.example.gitpulse.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/github")
@CrossOrigin(origins = "http://localhost:5173")
public class GitHubRepositoryController {

    @Autowired
    private GitHubService gitHubService;

    @PostMapping("/connect")
    public ResponseEntity<?> connectToRepository(@RequestBody Map<String, String> request) {
        try {
            String owner = request.get("owner");
            String repoName = request.get("repoName");
            Repository repository = gitHubService.connectToRepository(owner, repoName);
            return ResponseEntity.ok(repository);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/contributions/{owner}/{repo}")
    public ResponseEntity<?> getContributions(@PathVariable String owner, @PathVariable String repo) {
        try {
            return ResponseEntity.ok(gitHubService.getContributions(owner, repo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/contributors/{owner}/{repo}")
    public ResponseEntity<List<User>> getContributors(@PathVariable String owner, @PathVariable String repo) {
        try {
            return ResponseEntity.ok(gitHubService.getContributors(owner, repo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(List.of());
        }
    }

    @GetMapping("/stats/{owner}/{repo}")
    public ResponseEntity<?> getRepositoryStats(@PathVariable String owner, @PathVariable String repo) {
        try {
            return ResponseEntity.ok(gitHubService.getRepositoryStats(owner, repo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}