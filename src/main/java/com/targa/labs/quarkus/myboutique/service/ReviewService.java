package com.targa.labs.quarkus.myboutique.service;

import com.targa.labs.quarkus.myboutique.domain.Product;
import com.targa.labs.quarkus.myboutique.domain.Review;
import com.targa.labs.quarkus.myboutique.repository.ProductRepository;
import com.targa.labs.quarkus.myboutique.repository.ReviewRepository;
import com.targa.labs.quarkus.myboutique.web.dto.ReviewDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class ReviewService {
    private final Logger log = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ReviewService(ReviewRepository reviewRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }

    public static ReviewDto mapToDto(Review review) {
        if (review != null) {
            return new ReviewDto(
                    review.getId(),
                    review.getTitle(),
                    review.getDescription(),
                    review.getRating()
            );
        }
        return null;
    }

    public List<ReviewDto> findReviewsByProductId(Long id) {
        log.debug("Request to get all Reviews");
        return this.reviewRepository.findReviewsByProductId(id)
                .stream()
                .map(ReviewService::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewDto findById(Long id) {
        log.debug("Request to get Review : {}", id);
        return this.reviewRepository.findById(id).map(ReviewService::mapToDto).orElse(null);
    }

    public ReviewDto create(ReviewDto reviewDto, Long productId) {
        log.debug("Request to create Review : {} ofr the Product {}", reviewDto, productId);

        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Product with ID:" + productId + " was not found !"));

        Review savedReview = this.reviewRepository.saveAndFlush(
                new Review(
                        reviewDto.getTitle(),
                        reviewDto.getDescription(),
                        reviewDto.getRating()
                )
        );

        product.getReviews().add(savedReview);
        this.productRepository.saveAndFlush(product);

        return mapToDto(savedReview);
    }

    public void delete(Long reviewId) {
        log.debug("Request to delete Review : {}", reviewId);

        Review review = this.reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalStateException("Product with ID:" + reviewId + " was not found !"));

        Product product = this.productRepository.findProductByReviewId(reviewId);

        product.getReviews().remove(review);

        this.productRepository.saveAndFlush(product);
        this.reviewRepository.delete(review);
    }
}
