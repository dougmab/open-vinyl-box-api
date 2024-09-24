package com.github.dougmab.openvinylboxapi.dto;

import com.github.dougmab.openvinylboxapi.entity.Discount;
import org.hibernate.validator.constraints.Range;

import java.time.Instant;

public class DiscountDTO {
    @Range(min = 1, max = 100, message = "Percentage of discount must be between 1 and 100")
    private Short percentage;
    @Range(min = 1, message = "Duration in minutes is required")
    private Integer durationInMinutes;
    private Instant createdAt;
    private Instant endsAt;

    public DiscountDTO() {}

    public DiscountDTO(Long id, Short percentage, Integer durationInMinutes, Instant createdAt, Instant endsAt) {
        this.percentage = percentage;
        this.durationInMinutes = durationInMinutes;
        this.createdAt = createdAt;
        this.endsAt = endsAt;
    }

    public DiscountDTO(Discount entity) {
        percentage = entity.getPercentage();
        durationInMinutes = entity.getDurationInMinutes();
        createdAt = entity.getCreatedAt();
        endsAt = entity.getEndsAt();
    }

    public Short getPercentage() {
        return percentage;
    }

    public void setPercentage(Short percentage) {
        this.percentage = percentage;
    }

    public Integer getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(Integer durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Instant endsAt) {
        this.endsAt = endsAt;
    }
}
