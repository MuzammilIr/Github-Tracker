package com.example.github_tracker.dto;

import com.example.github_tracker.model.Contribution;
import com.example.github_tracker.model.ContributionDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContributionDetailDto {
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

    // New fromEntity method for ContributionDetail
    public static ContributionDetailDto fromEntity(ContributionDetail contributionDetail) {
        ContributionDetailDto dto = new ContributionDetailDto();
        dto.setId(contributionDetail.getId());
        dto.setUsername(contributionDetail.getContribution().getUser().getUsername());
        dto.setRepositoryName(contributionDetail.getContribution().getGitRepository().getName());
        dto.setContributionType(contributionDetail.getContribution().getContributionType().name());
        dto.setTimestamp(contributionDetail.getContribution().getTimestamp());
        dto.setDescription(contributionDetail.getContribution().getDescription());
        dto.setUrl(contributionDetail.getContribution().getUrl());
        dto.setLinesAdded(contributionDetail.getContribution().getLinesAdded());
        dto.setLinesRemoved(contributionDetail.getContribution().getLinesRemoved());
        dto.setFilesChanged(contributionDetail.getContribution().getFilesChanged());
        // If ContributionDetail has its own details list, you may handle it here similarly if needed
        return dto;
    }
}
