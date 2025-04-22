package com.example.github_tracker.repository;

import com.github.tracker.model.Contribution;
import com.github.tracker.model.Repository;
import com.github.tracker.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContributionRepository extends JpaRepository<Contribution, Long> {
    Page<Contribution> findByRepository(Repository repository, Pageable pageable);
    Page<Contribution> findByUser(User user, Pageable pageable);
    Page<Contribution> findByRepositoryAndUser(Repository repository, User user, Pageable pageable);

    @Query("SELECT c FROM Contribution c WHERE c.repository = :repository AND c.timestamp BETWEEN :startDate AND :endDate")
    Page<Contribution> findByRepositoryAndDateRange(
            @Param("repository") Repository repository,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT c FROM Contribution c WHERE c.user = :user AND c.timestamp BETWEEN :startDate AND :endDate")
    Page<Contribution> findByUserAndDateRange(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT c.user.username as username, COUNT(c) as count FROM Contribution c WHERE c.repository = :repository GROUP BY c.user ORDER BY count DESC")
    List<Object[]> findLeaderboardByRepository(@Param("repository") Repository repository, Pageable pageable);
}