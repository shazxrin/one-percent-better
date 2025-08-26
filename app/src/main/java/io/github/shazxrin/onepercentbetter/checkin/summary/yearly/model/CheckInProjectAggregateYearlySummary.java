package io.github.shazxrin.onepercentbetter.checkin.summary.yearly.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Table(name = "check_in_project_aggregate_yearly_summaries")
@Entity
public class CheckInProjectAggregateYearlySummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Integer> typeDistribution;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Integer> hourDistribution;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Integer> projectDistribution;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Integer> dayDistribution;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public CheckInProjectAggregateYearlySummary(
        int year,
        LocalDate startDate,
        LocalDate endDate,
        int noOfCheckIns,
        int streak
    ) {
        this();
        this.year = year;
        this.startDate = startDate;
        this.endDate = endDate;
        this.noOfCheckIns = noOfCheckIns;
        this.streak = streak;
    }

    public CheckInProjectAggregateYearlySummary() {
        this.typeDistribution = new LinkedHashMap<>();

        this.hourDistribution = new LinkedHashMap<>();
        for (var i = 0; i < 24; i++) {
            this.hourDistribution.put(String.valueOf(i), 0);
        }

        this.projectDistribution = new LinkedHashMap<>();

        this.dayDistribution = new LinkedHashMap<>();
        for (var i = 1; i <= 12; i++) {
            this.dayDistribution.put(String.valueOf(i), 0);
        }
    }

    public Map<String, Integer> getTypeDistribution() {
        return typeDistribution;
    }

    public void setTypeDistribution(Map<String, Integer> typeDistribution) {
        this.typeDistribution = typeDistribution;
    }

    public Map<String, Integer> getHourDistribution() {
        return hourDistribution;
    }

    public void setHourDistribution(Map<String, Integer> hourDistribution) {
        this.hourDistribution = hourDistribution;
    }

    public Map<String, Integer> getProjectDistribution() {
        return projectDistribution;
    }

    public void setProjectDistribution(Map<String, Integer> projectDistribution) {
        this.projectDistribution = projectDistribution;
    }

    public Map<String, Integer> getDayDistribution() {
        return dayDistribution;
    }

    public void setDayDistribution(Map<String, Integer> monthDistribution) {
        this.dayDistribution = monthDistribution;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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