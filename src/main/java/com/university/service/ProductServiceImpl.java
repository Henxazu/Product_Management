package com.university.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.entity.Product;
import com.university.repository.ProductRepository;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    // Constructor injection — Spring injects the repository automatically

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true) // optimization: no need for write transaction
    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Product saveProduct(Product product) {
    // Business rule: product codes must be unique
        if (product.getId() == null
                && repository.existsByProductCode(product.getProductCode())) {
            throw new IllegalArgumentException(
                    "Product code already exists: " + product.getProductCode());
        }
        return repository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
    // Verify the product exists before deleting
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> searchProducts(String keyword) {
        return repository.findByNameContaining(keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category) {
        return repository.findByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return repository.findAllCategories();
    }
}
