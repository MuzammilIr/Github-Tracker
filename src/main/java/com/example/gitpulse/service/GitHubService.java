
package com.example.gitpulse.service;

import com.example.gitpulse.model.Repository;
import com.example.gitpulse.model.User;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GitHubService {

    @Value("${github.api.token}")
    private String githubToken;

    private GitHub connectToGitHub() throws IOException {
        return GitHub.connectUsingOAuth(githubToken);
    }

    public User fetchGitHubUser(String username) throws Exception {
        GitHub github = connectToGitHub();
        GHUser ghUser = github.getUser(username);

        User user = new User();
        user.setId(String.valueOf(ghUser.getId()));
        user.setUsername(ghUser.getLogin());
        user.setAvatarUrl(ghUser.getAvatarUrl());
        user.setEmail(ghUser.getEmail());
        user.setRole("USER"); // Default role

        return user;
    }

    public Repository connectToRepository(String owner, String repoName) throws IOException {
        GitHub github = connectToGitHub();
        GHRepository ghRepo = github.getRepository(owner + "/" + repoName);

        Repository repository = new Repository();
        repository.setId(String.valueOf(ghRepo.getId()));
        repository.setName(ghRepo.getName());
        repository.setOwner(owner);
        repository.setUrl(ghRepo.getHtmlUrl().toString());
        repository.setDescription(ghRepo.getDescription());

        return repository;
    }

    public Map<String, Object> getContributions(String owner, String repoName) throws IOException {
        GitHub github = connectToGitHub();
        GHRepository ghRepo = github.getRepository(owner + "/" + repoName);

        List<Map<String, Object>> contributions = new ArrayList<>();

        // Fetch all commits (or a limited number to prevent overload)
        List<GHCommit> commits = ghRepo.listCommits().toList(); // ✅ Convert to List

        for (GHCommit commit : commits) {
            GHUser author = commit.getAuthor();

            if (author != null) {
                Map<String, Object> contribution = new HashMap<>();
                contribution.put("id", commit.getSHA1());

                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", String.valueOf(author.getId()));
                userMap.put("username", author.getLogin());
                userMap.put("avatarUrl", author.getAvatarUrl());

                contribution.put("user", userMap);
                contribution.put("type", "COMMIT");
                contribution.put("timestamp", commit.getCommitDate().toInstant().toString()); // ✅ directly from commit
                contribution.put("description", commit.getCommitShortInfo().getMessage()); // ✅ safely get short info
                contribution.put("url", commit.getHtmlUrl().toString());

                contribution.put("linesAdded", 0); // Optional: Parse diffs if you want actual numbers
                contribution.put("linesRemoved", 0);
                contribution.put("filesChanged", 0);

                Map<String, Object> repoMap = new HashMap<>();
                repoMap.put("id", String.valueOf(ghRepo.getId()));
                repoMap.put("name", ghRepo.getName());
                repoMap.put("url", ghRepo.getHtmlUrl().toString());
                repoMap.put("description", ghRepo.getDescription());

                contribution.put("repository", repoMap);

                contributions.add(contribution);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("contributions", contributions);

        return result;
    }
    public List<User> getContributors(String owner, String repoName) throws IOException {
        GitHub github = connectToGitHub();
        GHRepository ghRepo = github.getRepository(owner + "/" + repoName);

        List<User> contributors = new ArrayList<>();

        for (GHRepository.Contributor contributor : ghRepo.listContributors()) {
            User user = new User();
            user.setId(String.valueOf(contributor.getId()));
            user.setUsername(contributor.getLogin());
            user.setAvatarUrl(contributor.getAvatarUrl());
            user.setEmail(null); // Email is not available here
            user.setRole("CONTRIBUTOR");

            contributors.add(user);
        }

        return contributors;
    }
    public Map<String, Object> getRepositoryStats(String owner, String repoName) throws IOException {
        GitHub github = connectToGitHub();
        GHRepository ghRepo = github.getRepository(owner + "/" + repoName);

        Map<String, Object> stats = new HashMap<>();
        stats.put("stars", ghRepo.getStargazersCount());
        stats.put("forks", ghRepo.getForksCount());
        stats.put("watchers", ghRepo.getWatchersCount());
        stats.put("openIssues", ghRepo.getOpenIssueCount());
        stats.put("size", ghRepo.getSize());
        stats.put("language", ghRepo.getLanguage());

        return stats;
    }

}