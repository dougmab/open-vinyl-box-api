package com.github.dougmab.openvinylboxapi.dto;

import com.github.dougmab.openvinylboxapi.entity.Category;
import com.github.dougmab.openvinylboxapi.entity.Product;
import jakarta.validation.constraints.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ProductDTO {
    private Long id;
    @NotBlank(message = "Name is required")
    @Size(min = 5, max = 60, message = "Name must be between 2 and 80 characters")
    private String name;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotBlank(message = "Image URL is required")
    private String imgUrl;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date must be in the past or present")
    private Instant date;

    private List<CategoryDTO> categories = new ArrayList<>();

    public ProductDTO() {
    }

    public ProductDTO(Long id, String name, Double price, String imgUrl, Instant date) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imgUrl = imgUrl;
        this.date = date;
    }

    public ProductDTO(Product entity) {
        id = entity.getId();
        name = entity.getName();
        price = entity.getPrice();
        imgUrl = entity.getImgUrl();
        date = entity.getDate();
    }

    public ProductDTO(Product entity, Set<Category> categories) {
        this(entity);
        categories.forEach(category -> this.categories.add(new CategoryDTO(category)));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public List<CategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDTO> categories) {
        this.categories = categories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDTO that = (ProductDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(price, that.price) && Objects.equals(imgUrl, that.imgUrl) && Objects.equals(date, that.date) && Objects.equals(categories, that.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, imgUrl, date, categories);
    }
}
