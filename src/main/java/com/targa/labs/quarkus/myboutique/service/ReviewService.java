package com.targa.labs.quarkus.myboutique.service;

import com.targa.labs.quarkus.myboutique.domain.Review;
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

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
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

    public List<ReviewDto> findAll() {
        log.debug("Request to get all Reviews");
        return this.reviewRepository.findAll()
                .stream()
                .map(ReviewService::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewDto findById(Long id) {
        log.debug("Request to get Review : {}", id);
        return this.reviewRepository.findById(id).map(ReviewService::mapToDto).orElse(null);
    }

    public ReviewDto createDto(ReviewDto reviewDto) {
        log.debug("Request to create Review : {}", reviewDto);
        return mapToDto(create(reviewDto));
    }

    public Review create(ReviewDto reviewDto) {
        return this.reviewRepository.save(
                new Review(
                        reviewDto.getTitle(),
                        reviewDto.getDescription(),
                        reviewDto.getRating()
                )
        );
    }

    public void delete(Long id) {
        log.debug("Request to delete Review : {}", id);
        this.reviewRepository.deleteById(id);
    }
}
