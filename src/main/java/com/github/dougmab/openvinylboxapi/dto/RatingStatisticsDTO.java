package com.github.dougmab.openvinylboxapi.dto;

import com.github.dougmab.openvinylboxapi.entity.RatingStatistics;

public class RatingStatisticsDTO {
    private Integer totalRatings;

    private Integer totalStars;
    private Double averageRating;

    private Integer fiveStars;
    private Integer fourStars;
    private Integer threeStars;
    private Integer twoStars;
    private Integer oneStar;

    public RatingStatisticsDTO() {
    }

    public RatingStatisticsDTO(Integer totalRatings, Integer totalStars, Double averageRating, Integer fiveStars, Integer fourStars, Integer threeStars, Integer twoStars, Integer oneStar) {
        this.totalRatings = totalRatings;
        this.totalStars = totalStars;
        this.averageRating = averageRating;
        this.fiveStars = fiveStars;
        this.fourStars = fourStars;
        this.threeStars = threeStars;
        this.twoStars = twoStars;
        this.oneStar = oneStar;
    }

    public RatingStatisticsDTO(RatingStatistics statistics) {
        totalRatings = statistics.getTotalRatings();
        totalStars = statistics.getTotalStars();
        averageRating = statistics.getAverageRating();
        fiveStars = statistics.getFiveStars();
        fourStars = statistics.getFourStars();
        threeStars = statistics.getThreeStars();
        twoStars = statistics.getTwoStars();
        oneStar = statistics.getOneStar();
    }

    public Integer getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(Integer totalRatings) {
        this.totalRatings = totalRatings;
    }

    public Integer getTotalStars() {
        return totalStars;
    }

    public void setTotalStars(Integer totalStars) {
        this.totalStars = totalStars;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getFiveStars() {
        return fiveStars;
    }

    public void setFiveStars(Integer fiveStars) {
        this.fiveStars = fiveStars;
    }

    public Integer getFourStars() {
        return fourStars;
    }

    public void setFourStars(Integer fourStars) {
        this.fourStars = fourStars;
    }

    public Integer getThreeStars() {
        return threeStars;
    }

    public void setThreeStars(Integer threeStars) {
        this.threeStars = threeStars;
    }

    public Integer getTwoStars() {
        return twoStars;
    }

    public void setTwoStars(Integer twoStars) {
        this.twoStars = twoStars;
    }

    public Integer getOneStar() {
        return oneStar;
    }

    public void setOneStar(Integer oneStar) {
        this.oneStar = oneStar;
    }
}
