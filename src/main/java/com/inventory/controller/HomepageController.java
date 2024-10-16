package com.inventory.controller;

import com.inventory.model.Product;
import com.inventory.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomepageController {

    @Autowired
    private ProductService productService;

    // Homepage showing all products
    @GetMapping("/")
    public String showHomepage(Model model, @RequestParam(value = "search", required = false) String search) {
        List<Product> products;
        if (search != null && !search.isEmpty()) {
            products = productService.searchProduct(search);
        } else {
            products = productService.getAllProducts();
        }
        model.addAttribute("products", products);
        model.addAttribute("search", search);
        return "homepage";
    }
}
