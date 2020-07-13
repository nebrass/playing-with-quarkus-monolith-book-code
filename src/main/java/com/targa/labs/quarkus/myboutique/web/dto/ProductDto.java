package com.targa.labs.quarkus.myboutique.web.dto;

import java.math.BigDecimal;
import java.util.Set;

/**
 * @author Nebrass Lamouchi
 */
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String status;
    private Integer salesCounter;
    private Set<ReviewDto> reviews;
    private Long categoryId;

    public ProductDto() {
    }

    public ProductDto(Long id, String name, String description,
                      BigDecimal price, String status, Integer salesCounter,
                      Set<ReviewDto> reviews, Long categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = status;
        this.salesCounter = salesCounter;
        this.reviews = reviews;
        this.categoryId = categoryId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSalesCounter() {
        return salesCounter;
    }

    public void setSalesCounter(Integer salesCounter) {
        this.salesCounter = salesCounter;
    }

    public Set<ReviewDto> getReviews() {
        return reviews;
    }

    public void setReviews(Set<ReviewDto> reviews) {
        this.reviews = reviews;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
