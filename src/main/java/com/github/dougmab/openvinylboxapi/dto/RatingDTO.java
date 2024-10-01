package com.github.dougmab.openvinylboxapi.dto;

import com.github.dougmab.openvinylboxapi.entity.Rating;

public class RatingDTO {
    private Short value;
    private String comment;

    public RatingDTO() {
    }

    public RatingDTO(Short value, String comment) {
        this.value = value;
        this.comment = comment;
    }

    public RatingDTO(Rating rating) {
        value = rating.getRatingValue();
        comment = rating.getComment();
    }

    public Short getValue() {
        return value;
    }

    public void setValue(Short value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
