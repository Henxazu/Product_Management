package com.university.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.entity.Product;
import com.university.repository.ProductRepository;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "productCode", "name", "price", "quantity", "category", "createdAt"
    );

    private final ProductRepository repository;
    // Constructor injection — Spring injects the repository automatically

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    private Sort buildSort(String sortBy, String sortDir) {
        String field = (sortBy != null && ALLOWED_SORT_FIELDS.contains(sortBy)) ? sortBy : "id";
        Sort sort = Sort.by(field);
        return "desc".equalsIgnoreCase(sortDir) ? sort.descending() : sort.ascending();
    }

    @Override
    @Transactional(readOnly = true) // optimization: no need for write transaction
    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts(String sortBy, String sortDir) {
        return repository.findAll(buildSort(sortBy, sortDir));
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
    public List<Product> searchProducts(String keyword, String sortBy, String sortDir) {
        return repository.findByNameContaining(keyword, buildSort(sortBy, sortDir));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category) {
        return repository.findByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category, String sortBy, String sortDir) {
        return repository.findByCategory(category, buildSort(sortBy, sortDir));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return repository.findAllCategories();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> advancedSearch(String name, String category, BigDecimal minPrice, BigDecimal maxPrice) {
        return advancedSearch(name, category, minPrice, maxPrice, "id", "asc");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> advancedSearch(String name, String category, BigDecimal minPrice, BigDecimal maxPrice,
            String sortBy, String sortDir) {
        String normalizedName = (name != null && !name.isBlank()) ? name.trim() : null;
        String normalizedCategory = (category != null && !category.isBlank()) ? category.trim() : null;
        return repository.advancedSearch(normalizedName, normalizedCategory, minPrice, maxPrice,
                buildSort(sortBy, sortDir));
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalProductCount() {
        return repository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getProductCountByCategory() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (Object[] row : repository.countProductsByCategory()) {
            counts.put((String) row[0], ((Number) row[1]).longValue());
        }
        return counts;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalInventoryValue() {
        BigDecimal total = repository.calculateTotalInventoryValue();
        return total != null ? total : BigDecimal.ZERO;
    }
}
