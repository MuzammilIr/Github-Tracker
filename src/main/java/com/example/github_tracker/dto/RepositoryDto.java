package com.example.github_tracker.dto;

import com.example.github_tracker.model.GitRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryDto {
    private Long id;
    private String githubRepoId;
    private String name;
    private String url;

    public static RepositoryDto fromEntity(GitRepository gitRepository) {
        RepositoryDto dto = new RepositoryDto();
        dto.setId(gitRepository.getId());
        dto.setGithubRepoId(gitRepository.getGithubRepoId());
        dto.setName(gitRepository.getName());
        dto.setUrl(gitRepository.getUrl());
        return dto;
    }
}