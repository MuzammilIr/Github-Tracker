package com.example.github_tracker.repository;

import com.github.tracker.model.Contribution;
import com.github.tracker.model.ContributionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContributionDetailRepository extends JpaRepository<ContributionDetail, Long> {
    List<ContributionDetail> findByContribution(Contribution contribution);
}