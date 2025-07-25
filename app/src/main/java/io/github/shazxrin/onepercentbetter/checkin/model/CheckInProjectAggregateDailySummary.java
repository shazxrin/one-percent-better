package io.github.shazxrin.onepercentbetter.checkin.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@EntityListeners(AuditingEntityListener.class)
@Table(name = "check_in_project_aggregate_daily_summaries")
@Entity
public class CheckInProjectAggregateDailySummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int noOfCheckIns;

    @Column(nullable = false)
    private int streak;

    @CreatedDate
    private LocalDate createdAt;

    @LastModifiedDate
    private LocalDate updatedAt;

    public CheckInProjectAggregateDailySummary(LocalDate date, int noOfCheckIns, int streak) {
        this.date = date;
        this.noOfCheckIns = noOfCheckIns;
        this.streak = streak;
    }

    public CheckInProjectAggregateDailySummary() { }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
}
