package com.example.github_tracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "contribution_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContributionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contribution_id", nullable = false)
    private Contribution contribution;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "lines_added")
    private Integer linesAdded;

    @Column(name = "lines_removed")
    private Integer linesRemoved;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}