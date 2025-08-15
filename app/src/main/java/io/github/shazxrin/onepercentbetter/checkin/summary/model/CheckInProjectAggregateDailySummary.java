package io.github.shazxrin.onepercentbetter.checkin.summary.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Table(name = "check_in_project_aggregate_daily_summaries")
@Entity
public class CheckInProjectAggregateDailySummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate date;

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

    @CreatedDate
    private LocalDate createdAt;

    @LastModifiedDate
    private LocalDate updatedAt;

    public CheckInProjectAggregateDailySummary(LocalDate date, int noOfCheckIns, int streak) {
        this();
        this.date = date;
        this.noOfCheckIns = noOfCheckIns;
        this.streak = streak;
    }

    public CheckInProjectAggregateDailySummary() {
        this.typeDistribution = new LinkedHashMap<>();

        this.hourDistribution = new LinkedHashMap<>();
        for (var i = 0; i < 24; i++) {
            this.hourDistribution.put(String.valueOf(i), 0);
        }
    }

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
