package com.example.github_tracker.repository;

import com.example.github_tracker.model.GitRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryRepository extends JpaRepository<GitRepository, Long> {
    Optional<GitRepository> findByGithubRepoId(String githubRepoId);
    boolean existsByGithubRepoId(String githubRepoId);
}
