package com.university.dto;

import com.university.entity.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashBoardView {
    private final long totalProducts;
    private final Map<String, Long> productsPerCategory;
    private final BigDecimal totalInventoryValue;
    private final List<Product> lowStockProducts;

    public DashBoardView(long totalProducts,
            Map<String, Long> productsPerCategory,
            BigDecimal totalInventoryValue,
            List<Product> lowStockProducts) {
        this.totalProducts = totalProducts;
        this.productsPerCategory = productsPerCategory;
        this.totalInventoryValue = totalInventoryValue;
        this.lowStockProducts = lowStockProducts;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public Map<String, Long> getProductsPerCategory() {
        return productsPerCategory;
    }

    public BigDecimal getTotalInventoryValue() {
        return totalInventoryValue;
    }

    public List<Product> getLowStockProducts() {
        return lowStockProducts;
    }
}
