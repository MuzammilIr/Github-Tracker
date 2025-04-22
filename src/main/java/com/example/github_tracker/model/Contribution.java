package com.example.github_tracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "contributions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repo_id", nullable = false)
    private Repository repository;

    @Column(name = "contribution_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContributionType contributionType;

    @Column(name = "github_id")
    private String githubId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column
    private String description;

    @Column
    private String url;

    @Column(name = "lines_added")
    private Integer linesAdded;

    @Column(name = "lines_removed")
    private Integer linesRemoved;

    @Column(name = "files_changed")
    private Integer filesChanged;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "contribution", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContributionDetail> details = new HashSet<>();

    public enum ContributionType {
        COMMIT, PULL_REQUEST, REVIEW
    }
}