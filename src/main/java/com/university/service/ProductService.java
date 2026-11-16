package com.university.service;

import java.util.List;
import java.util.Optional;

import com.university.entity.Product;

public interface ProductService {

    List<Product> getAllProducts();

    Optional<Product> getProductById(Long id);

    Product saveProduct(Product product);

    void deleteProduct(Long id);

    List<Product> searchProducts(String keyword);

    List<Product> getProductsByCategory(String category);

    List<String> getAllCategories();
}
