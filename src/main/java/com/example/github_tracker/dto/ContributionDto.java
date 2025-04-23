package com.example.github_tracker.dto;

import com.example.github_tracker.model.Contribution;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContributionDto {
    private Long id;
    private String username;
    private String repositoryName;
    private String contributionType;
    private LocalDateTime timestamp;
    private String description;
    private String url;
    private Integer linesAdded;
    private Integer linesRemoved;
    private Integer filesChanged;
    private List<ContributionDetailDto> details;

    public static ContributionDetailDto fromEntity(Contribution contribution) {
        ContributionDetailDto dto = new ContributionDetailDto();
        dto.setId(contribution.getId());
        dto.setUsername(contribution.getUser().getUsername());
        dto.setRepositoryName(contribution.getGitRepository().getName());
        dto.setContributionType(contribution.getContributionType().name());
        dto.setTimestamp(contribution.getTimestamp());
        dto.setDescription(contribution.getDescription());
        dto.setUrl(contribution.getUrl());
        dto.setLinesAdded(contribution.getLinesAdded());
        dto.setLinesRemoved(contribution.getLinesRemoved());
        dto.setFilesChanged(contribution.getFilesChanged());

        if (contribution.getDetails() != null && !contribution.getDetails().isEmpty()) {
            dto.setDetails(contribution.getDetails().stream()
                    .map(ContributionDetailDto::fromEntity)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}