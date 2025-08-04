package io.github.shazxrin.onepercentbetter.checkin.summary.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Table(name = "check_in_project_aggregate_weekly_summaries")
@Entity
public class CheckInProjectAggregateWeeklySummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int weekNo;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private int noOfCheckIns;

    @Column(nullable = false)
    private int streak;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public CheckInProjectAggregateWeeklySummary(
        int weekNo,
        int year,
        LocalDate startDate,
        LocalDate endDate,
        int noOfCheckIns,
        int streak
    ) {
        this.weekNo = weekNo;
        this.year = year;
        this.startDate = startDate;
        this.endDate = endDate;
        this.noOfCheckIns = noOfCheckIns;
        this.streak = streak;
    }

    public CheckInProjectAggregateWeeklySummary() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getWeekNo() {
        return weekNo;
    }

    public void setWeekNo(int weekNo) {
        this.weekNo = weekNo;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getNoOfCheckIns() {
        return noOfCheckIns;
    }

    public void setNoOfCheckIns(int noOfCheckIns) {
        this.noOfCheckIns = noOfCheckIns;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

