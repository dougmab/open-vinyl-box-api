package com.github.dougmab.openvinylboxapi.entity;

import com.github.dougmab.openvinylboxapi.dto.RatingDTO;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
public class Rating implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private Short ratingValue;
    private String comment;

    public Rating() {
    }

    public Rating(Product product, User user, Short ratingValue, String comment) {
        this.product = product;
        this.user = user;
        this.ratingValue = ratingValue;
        this.comment = comment;
    }

    public Rating(RatingDTO ratingDTO) {
        this.ratingValue = ratingDTO.getValue();
        this.comment = ratingDTO.getComment();
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Short getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Short ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
