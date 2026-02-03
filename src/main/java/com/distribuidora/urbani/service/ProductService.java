package com.distribuidora.urbani.service;

import com.distribuidora.urbani.dto.ProductRequest;
import com.distribuidora.urbani.entity.Product;
import com.distribuidora.urbani.exceptions.ResourceNotFoundException;
import com.distribuidora.urbani.repository.ProductRepository;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .stock(productRequest.stock())
                .build();

        return productRepository.save(product);
    }

    public Product updateProduct(UUID id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setName(productRequest.name());
        product.setDescription(productRequest.description());
        product.setPrice(productRequest.price());
        product.setStock(productRequest.stock());

        return productRepository.save(product);
    }

    public void deleteProduct(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
    }

    public Product getProduct(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void reduceStock(UUID id, int quantity) {
        Product product = getProduct(id);
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    public boolean stockAvailable(int stock, @Min(value = 1, message = "La cantidad mínima es 1") Integer quantity) {
        return stock >= quantity;
    }
}
