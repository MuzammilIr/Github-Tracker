package com.example.github_tracker.dto;

import com.github.tracker.model.Repository;
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

    public static RepositoryDto fromEntity(Repository repository) {
        RepositoryDto dto = new RepositoryDto();
        dto.setId(repository.getId());
        dto.setGithubRepoId(repository.getGithubRepoId());
        dto.setName(repository.getName());
        dto.setUrl(repository.getUrl());
        return dto;
    }
}