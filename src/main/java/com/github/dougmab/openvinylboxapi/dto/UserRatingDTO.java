package com.github.dougmab.openvinylboxapi.dto;

public class UserRatingDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private Short rating;
    private String comment;

    public UserRatingDTO() {
    }

    public UserRatingDTO(Long id, String firstName, String lastName, Short rating, String comment) {
        this.userId = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.rating = rating;
        this.comment = comment;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Short getRating() {
        return rating;
    }

    public void setRating(Short rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
