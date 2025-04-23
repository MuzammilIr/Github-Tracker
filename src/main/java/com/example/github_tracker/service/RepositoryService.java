package com.example.github_tracker.service;

import com.example.github_tracker.dto.RepositoryDto;
import com.example.github_tracker.exception.ResourceNotFoundException;
import com.example.github_tracker.model.Repository;
import com.example.github_tracker.repository.RepositoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RepositoryService {

    @Autowired
    private RepositoryRepository repositoryRepository;

    @Autowired
    private GitHubApiService gitHubApiService;

    public List<RepositoryDto> getAllRepositories() {
        return repositoryRepository.findAll().stream()
                .map(RepositoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    public RepositoryDto getRepositoryById(Long id) {
        Repository repository = repositoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GitRepository not found with id: " + id));

        return RepositoryDto.fromEntity(repository);
    }

    public RepositoryDto addRepository(String githubRepoId, String name, String url, String webhookSecret) {
        Repository repository = new Repository();
        repository.setGithubRepoId(githubRepoId);
        repository.setName(name);
        repository.setUrl(url);
        repository.setWebhookSecret(webhookSecret);

        repository = repositoryRepository.save(repository);
        return RepositoryDto.fromEntity(repository);
    }

    public void deleteRepository(Long id) {
        if (!repositoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("GitRepository not found with id: " + id);
        }

        repositoryRepository.deleteById(id);
    }

    public Repository getOrCreateRepository(String githubRepoId, String name, String url) {
        return repositoryRepository.findByGithubRepoId(githubRepoId)
                .orElseGet(() -> {
                    Repository newRepo = new Repository();
                    newRepo.setGithubRepoId(githubRepoId);
                    newRepo.setName(name);
                    newRepo.setUrl(url);
                    return repositoryRepository.save(newRepo);
                });
    }

    public boolean configureWebhook(Long repositoryId, String accessToken) {
        Repository repository = repositoryRepository.findById(repositoryId)
                .orElseThrow(() -> new ResourceNotFoundException("GitRepository not found with id: " + repositoryId));

        // This would call GitHub API to set up a webhook
        String webhookSecret = gitHubApiService.configureWebhook(repository.getUrl(), accessToken);

        if (webhookSecret != null) {
            repository.setWebhookSecret(webhookSecret);
            repositoryRepository.save(repository);
            return true;
        }

        return false;
    }
}