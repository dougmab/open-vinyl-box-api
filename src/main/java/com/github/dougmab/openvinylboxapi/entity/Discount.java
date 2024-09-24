package com.github.dougmab.openvinylboxapi.entity;

import com.github.dougmab.openvinylboxapi.dto.DiscountDTO;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Entity
public class Discount implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Short percentage;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    @CreationTimestamp
    private Instant createdAt;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    @CreationTimestamp
    private Instant endsAt;

    private Integer durationInMinutes;

    public Discount() {}

    public Discount(Long id, Short percentage, Integer durationInMinutes) {
        this.id = id;
        this.percentage = percentage;
        this.durationInMinutes = durationInMinutes;
        endsAt = Instant.now().plusSeconds(durationInMinutes * 60);
    }

    public Discount(DiscountDTO dto) {
        percentage = dto.getPercentage();
        durationInMinutes = dto.getDurationInMinutes();
        endsAt = Instant.now().plusSeconds(durationInMinutes * 60);
    }

    @PostPersist
    public void prePersist() {
        endsAt = Instant.now().plusSeconds(durationInMinutes * 60);
    }

    public Long getId() {
        return id;
    }

    public Short getPercentage() {
        return percentage;
    }

    public void setPercentage(Short percentage) {
        this.percentage = percentage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Integer getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(Integer durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public Instant getEndsAt() {
        return endsAt;
    }
}
