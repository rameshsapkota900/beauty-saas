package com.example.beautysaas.service;

import com.example.beautysaas.dto.product.ProductCreateRequest;
import com.example.beautysaas.dto.product.ProductDto;
import com.example.beautysaas.dto.product.ProductUpdateRequest;
import com.example.beautysaas.entity.Category;
import com.example.beautysaas.entity.Parlour;
import com.example.beautysaas.entity.Product;
import com.example.beautysaas.entity.User;
import com.example.beautysaas.exception.BeautySaasApiException;
import com.example.beautysaas.exception.ResourceNotFoundException;
import com.example.beautysaas.repository.CategoryRepository;
import com.example.beautysaas.repository.ParlourRepository;
import com.example.beautysaas.repository.ProductRepository;
import com.example.beautysaas.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ParlourRepository parlourRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public ProductService(ProductRepository productRepository, ParlourRepository parlourRepository, CategoryRepository categoryRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.parlourRepository = parlourRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public ProductDto addProduct(String adminEmail, UUID parlourId, ProductCreateRequest createRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Parlour parlour = parlourRepository.findById(parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Parlour", "id", parlourId));
        Category category = categoryRepository.findById(createRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", createRequest.getCategoryId()));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(parlourId)){
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not associated with this parlour.");
        }
        if (!category.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Category does not belong to the specified parlour.");
        }
        if (productRepository.existsByParlourIdAndNameIgnoreCase(parlourId, createRequest.getName())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Product with this name already exists for this parlour.");
        }

        Product product = Product.builder()
                .parlour(parlour)
                .name(createRequest.getName())
                .description(createRequest.getDescription())
                .price(createRequest.getPrice())
                .stockQuantity(createRequest.getStockQuantity())
                .category(category)
                .isActive(createRequest.getIsActive())
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Product added: {}", savedProduct.getId());
        return mapToDto(savedProduct);
    }

    public Page<ProductDto> listProducts(UUID parlourId, Pageable pageable) {
        Page<Product> products = productRepository.findByParlourId(parlourId, pageable);
        return products.map(this::mapToDto);
    }

    public ProductDto getProductDetail(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return mapToDto(product);
        }

    @Transactional
    public ProductDto updateProduct(String adminEmail, UUID id, ProductUpdateRequest updateRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(product.getParlour().getId())) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized to update this product.");
        }

        if (updateRequest.getName() != null && !updateRequest.getName().equalsIgnoreCase(product.getName())) {
            if (productRepository.existsByParlourIdAndNameIgnoreCase(product.getParlour().getId(), updateRequest.getName())) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Product with this name already exists for this parlour.");
            }
            product.setName(updateRequest.getName());
        }
        if (updateRequest.getDescription() != null) {
            product.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getPrice() != null) {
            product.setPrice(updateRequest.getPrice());
        }
        if (updateRequest.getStockQuantity() != null) {
            product.setStockQuantity(updateRequest.getStockQuantity());
        }
        if (updateRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", updateRequest.getCategoryId()));
            if (!category.getParlour().getId().equals(product.getParlour().getId())) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "New category does not belong to the same parlour.");
            }
            product.setCategory(category);
        }
        if (updateRequest.getIsActive() != null) {
            product.setIsActive(updateRequest.getIsActive());
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated: {}", updatedProduct.getId());
        return mapToDto(updatedProduct);
    }

    @Transactional
    public void deleteProduct(String adminEmail, UUID id) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(product.getParlour().getId())) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized to delete this product.");
        }

        productRepository.delete(product);
        log.info("Product deleted: {}", id);
    }

    private ProductDto mapToDto(Product product) {
        ProductDto dto = modelMapper.map(product, ProductDto.class);
        dto.setParlourId(product.getParlour().getId());
        dto.setCategoryName(product.getCategory().getName());
        return dto;
    }
}
