package com.targa.labs.quarkus.myboutique.service;

import com.targa.labs.quarkus.myboutique.domain.Category;
import com.targa.labs.quarkus.myboutique.repository.CategoryRepository;
import com.targa.labs.quarkus.myboutique.repository.ProductRepository;
import com.targa.labs.quarkus.myboutique.web.dto.CategoryDto;
import com.targa.labs.quarkus.myboutique.web.dto.ProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class CategoryService {

    private final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public static CategoryDto mapToDto(Category category, Long productsCount) {
        if (category != null) {
            return new CategoryDto(
                    category.getId(),
                    category.getName(),
                    category.getDescription(),
                    productsCount
            );
        }
        return null;
    }

    public List<CategoryDto> findAll() {
        log.debug("Request to get all Categories");
        return this.categoryRepository.findAll()
                .stream()
                .map(category -> mapToDto(category, productRepository.countAllByCategoryId(category.getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryDto findById(Long id) {
        log.debug("Request to get Category : {}", id);
        return this.categoryRepository.findById(id)
                .map(category -> mapToDto(category, productRepository.countAllByCategoryId(category.getId())))
                .orElse(null);
    }

    public CategoryDto create(CategoryDto categoryDto) {
        log.debug("Request to create Category : {}", categoryDto);
        return mapToDto(this.categoryRepository
                .save(new Category(categoryDto.getName(), categoryDto.getDescription())), 0L);
    }

    public void delete(Long id) {
        log.debug("Request to delete Category : {}", id);
        log.debug("Deleting all products for the Category : {}", id);
        this.productRepository.deleteAllByCategoryId(id);
        log.debug("Deleting Category : {}", id);
        this.categoryRepository.deleteById(id);
    }

    public List<ProductDto> findProductsByCategoryId(Long id) {
        return this.productRepository.findAllByCategoryId(id)
                .stream()
                .map(ProductService::mapToDto)
                .collect(Collectors.toList());
    }
}
