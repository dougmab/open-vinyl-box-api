package com.github.dougmab.openvinylboxapi.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class RatingStatistics implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    private Integer totalRatings;

    private Integer totalStars;
    private Integer fiveStars;
    private Integer fourStars;
    private Integer threeStars;
    private Integer twoStars;
    private Integer oneStar;

    public RatingStatistics() {
    }

    public RatingStatistics(Product product) {
        this.product = product;
        totalRatings = 0;
        totalStars = 0;
        fiveStars = 0;
        fourStars = 0;
        threeStars = 0;
        twoStars = 0;
        oneStar = 0;
    }

    public void addRating(Rating rating) {
        totalRatings++;
        totalStars += rating.getRatingValue();
        adjustCount(rating.getRatingValue().intValue(), 1);
    }

    public void removeRating(Rating rating) {
        totalRatings--;
        totalStars -= rating.getRatingValue();
        adjustCount(rating.getRatingValue().intValue(), -1);
    }

    public void updateRating(Short oldStars, Short newStars) {
        totalStars += newStars - oldStars;
        adjustCount(oldStars.intValue(), -1);
        adjustCount(newStars.intValue(), 1);
    }

    public void adjustCount(Integer stars, Integer increment) {
        switch (stars) {
            case 5 -> fiveStars += increment;
            case 4 -> fourStars += increment;
            case 3 -> threeStars += increment;
            case 2 -> twoStars += increment;
            case 1 -> oneStar += increment;
        }
    }

    public Double getAverageRating() {
        if (totalRatings.equals(0)) return 0.0;
        double result = totalStars / (double) totalRatings;
        return Math.round(result * 10.0) / 10.0; // round to 1 decimal place
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Integer getTotalRatings() {
        return totalRatings;
    }

    public Integer getTotalStars() {
        return totalStars;
    }

    public Integer getFiveStars() {
        return fiveStars;
    }

    public Integer getFourStars() {
        return fourStars;
    }

    public Integer getThreeStars() {
        return threeStars;
    }

    public Integer getTwoStars() {
        return twoStars;
    }

    public Integer getOneStar() {
        return oneStar;
    }
}
