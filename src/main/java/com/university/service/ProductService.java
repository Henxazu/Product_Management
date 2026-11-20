package com.university.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.university.dto.DashBoardView;
import com.university.entity.Product;

public interface ProductService {

    List<Product> getAllProducts();

    List<Product> getAllProducts(String sortBy, String sortDir);

    Optional<Product> getProductById(Long id);

    Product saveProduct(Product product);

    void deleteProduct(Long id);

    List<Product> searchProducts(String keyword);

    List<Product> searchProducts(String keyword, String sortBy, String sortDir);

    List<Product> getProductsByCategory(String category);

    List<Product> getProductsByCategory(String category, String sortBy, String sortDir);

    List<String> getAllCategories();

    List<Product> advancedSearch(String name, String category, BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> advancedSearch(String name, String category, BigDecimal minPrice, BigDecimal maxPrice,
            String sortBy, String sortDir);

    long getTotalProductCount();

    Map<String, Long> getProductCountByCategory();

    BigDecimal getTotalInventoryValue();

    DashBoardView getDashBoardData();
}
