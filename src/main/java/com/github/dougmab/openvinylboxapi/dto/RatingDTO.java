package com.github.dougmab.openvinylboxapi.dto;

public class RatingDTO {
    private Short value;
    private String comment;

    public RatingDTO() {
    }

    public RatingDTO(Short value, String comment) {
        this.value = value;
        this.comment = comment;
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
