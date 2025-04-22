package com.example.github_tracker.repository;

import com.github.tracker.model.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryRepository extends JpaRepository<Repository, Long> {
    Optional<Repository> findByGithubRepoId(String githubRepoId);
    boolean existsByGithubRepoId(String githubRepoId);
}