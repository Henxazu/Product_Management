package com.university.controller;

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

    @GetMapping
    public String listProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            Model model) {
        List<Product> products;
        if (keyword != null && !keyword.isBlank()) {
            products = productService.searchProducts(keyword);
            model.addAttribute("keyword", keyword);
        } else if (category != null && !category.isBlank()) {
            products = productService.getProductsByCategory(category);
            model.addAttribute("selectedCategory", category);
        } else {
            products = productService.getAllProducts();
        }
        model.addAttribute("products", products);
        model.addAttribute("categories", productService.getAllCategories());
        return "product-list";
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
            productService.saveProduct(product);
            String msg = product.getId() == null
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
