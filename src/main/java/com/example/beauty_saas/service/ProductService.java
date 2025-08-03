package com.example.beauty_saas.service;

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
    private final CategoryRepository categoryRepository;
    private final ParlourRepository parlourRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ParlourRepository parlourRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.parlourRepository = parlourRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public ProductDto createProduct(String adminEmail, UUID parlourId, ProductCreateRequest createRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        Parlour parlour = parlourRepository.findById(parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Parlour", "id", parlourId));
        Category category = categoryRepository.findById(createRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", createRequest.getCategoryId()));

        if (!category.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Category does not belong to the specified parlour.");
        }

        if (productRepository.existsByParlourIdAndNameIgnoreCase(parlourId, createRequest.getName())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Product with name '" + createRequest.getName() + "' already exists in this parlour.");
        }

        Product product = modelMapper.map(createRequest, Product.class);
        product.setParlour(parlour);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        log.info("Product created: {}", savedProduct.getId());
        return mapToDto(savedProduct);
    }

    public ProductDto getProductById(String userEmail, UUID parlourId, UUID productId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (!product.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Product does not belong to the specified parlour.");
        }

        // Ensure user is authorized to view this parlour's data
        if (user.getRole().getName().equals("ADMIN") && (user.getParlour() == null || !user.getParlour().getId().equals(parlourId))) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "Admin is not authorized for this parlour.");
        }
        // Customers can view any parlour's products for now, but this can be restricted if needed.

        log.debug("Fetching product: {}", productId);
        return mapToDto(product);
    }

    public Page<ProductDto> getAllProducts(String userEmail, UUID parlourId, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        // Ensure user is authorized to view this parlour's data
        if (user.getRole().getName().equals("ADMIN") && (user.getParlour() == null || !user.getParlour().getId().equals(parlourId))) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "Admin is not authorized for this parlour.");
        }
        // Customers can view any parlour's products for now, but this can be restricted if needed.

        log.debug("Fetching all products for parlour: {}", parlourId);
        Page<Product> products = productRepository.findByParlourId(parlourId, pageable);
        return products.map(this::mapToDto);
    }

    @Transactional
    public ProductDto updateProduct(String adminEmail, UUID parlourId, UUID productId, ProductUpdateRequest updateRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (!product.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Product does not belong to the specified parlour.");
        }

        if (updateRequest.getName() != null && !updateRequest.getName().equalsIgnoreCase(product.getName())) {
            if (productRepository.existsByParlourIdAndNameIgnoreCase(parlourId, updateRequest.getName())) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Product with name '" + updateRequest.getName() + "' already exists in this parlour.");
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
            if (!category.getParlour().getId().equals(parlourId)) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Category does not belong to the specified parlour.");
            }
            product.setCategory(category);
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated: {}", updatedProduct.getId());
        return mapToDto(updatedProduct);
    }

    @Transactional
    public void deleteProduct(String adminEmail, UUID parlourId, UUID productId) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (!product.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Product does not belong to the specified parlour.");
        }

        productRepository.delete(product);
        log.info("Product deleted: {}", productId);
    }

    private ProductDto mapToDto(Product product) {
        ProductDto dto = modelMapper.map(product, ProductDto.class);
        dto.setParlourId(product.getParlour().getId());
        dto.setParlourName(product.getParlour().getName());
        dto.setCategoryName(product.getCategory().getName());
        return dto;
    }
}
