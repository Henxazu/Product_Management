package com.university.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.university.entity.Product;
import com.university.service.ProductService;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    // GET /products
    // GET /products?category=Electronics
    // GET /products?keyword=laptop
    // GET /products?sortBy=price&sortDir=desc

    @GetMapping
    public String listProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {
        String normalizedDir = "desc".equalsIgnoreCase(sortDir) ? "desc" : "asc";
        List<Product> products;
        if (keyword != null && !keyword.isBlank()) {
            products = productService.searchProducts(keyword, sortBy, normalizedDir);
            model.addAttribute("keyword", keyword);
        } else if (category != null && !category.isBlank()) {
            products = productService.getProductsByCategory(category, sortBy, normalizedDir);
            model.addAttribute("selectedCategory", category);
        } else {
            products = productService.getAllProducts(sortBy, normalizedDir);
        }
        model.addAttribute("products", products);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("baseUrl", "/products");
        addSortAttributes(model, sortBy, normalizedDir);
        addStatistics(model);
        return "product-list";
    }

    // GET /products/advanced-search?name=...&category=...&minPrice=...&maxPrice=...&sortBy=...&sortDir=...
    @GetMapping("/advanced-search")
    public String advancedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {
        String normalizedDir = "desc".equalsIgnoreCase(sortDir) ? "desc" : "asc";
        List<Product> products = productService.advancedSearch(name, category, minPrice, maxPrice,
                sortBy, normalizedDir);
        model.addAttribute("products", products);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("baseUrl", "/products/advanced-search");
        model.addAttribute("name", name);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        addSortAttributes(model, sortBy, normalizedDir);
        addStatistics(model);
        return "product-list";
    }

    private void addSortAttributes(Model model, String sortBy, String sortDir) {
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", "asc".equals(sortDir) ? "desc" : "asc");
    }

    private void addStatistics(Model model) {
        model.addAttribute("totalProducts", productService.getTotalProductCount());
        model.addAttribute("categoryCounts", productService.getProductCountByCategory());
        model.addAttribute("totalInventoryValue", productService.getTotalInventoryValue());
    }

    // GET /products/new
    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", productService.getAllCategories());
        return "product-form";
    }

    // GET /products/edit/1
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model,
            RedirectAttributes redirectAttributes) {
        return productService.getProductById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    model.addAttribute("categories", productService.getAllCategories());
                    return "product-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Product not found");
                    return "redirect:/products";
                });
    }

    // POST /products/save
    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") Product product,
            RedirectAttributes redirectAttributes) {
        try {
            boolean isNew = product.getId() == null;
            productService.saveProduct(product);
            String msg = isNew
                    ? "Product added successfully!"
                    : "Product updated successfully!";
            redirectAttributes.addFlashAttribute("message", msg);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving product: "
                    + e.getMessage());
        }
        return "redirect:/products";
    }

    // GET /products/delete/1
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("message", "Product delete successfully !");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }
}
