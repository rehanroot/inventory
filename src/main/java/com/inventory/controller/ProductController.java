package com.inventory.controller;

import com.inventory.model.Product;
import com.inventory.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    // Path to save uploaded images
    private final String uploadDir = "C:/uploads/";

    // Display the form to add a new product
    @GetMapping("/products")
    public String showProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "product-form"; // Return the name of the Thymeleaf template
    }

    // Handle form submission to add a new product
    @PostMapping("/products")
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam("image") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        // Save image and set the path
        try {
            saveImage(file, product);
            productService.saveProduct(product);  // Save product with image path
            redirectAttributes.addFlashAttribute("successMessage", "Product added successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload image!");
        }
        return "redirect:/products"; // Redirect to the product list (to be created)
    }

    // Save the uploaded image
    private void saveImage(MultipartFile file, Product product) throws IOException {
        if (!file.isEmpty()) {
            // Ensure the directory exists
            Files.createDirectories(Paths.get(uploadDir));
            // Save the file
            Path path = Paths.get(uploadDir + file.getOriginalFilename());
            Files.write(path, file.getBytes());
            // Set the image path in the product
            product.setImagePath(path.toString());
        }
    }

    // Download all products as a CSV file using built-in Java methods
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadCSV() {
        List<Product> products = productService.getAllProducts();
        ByteArrayInputStream stream = generateCsv(products);

        InputStreamResource resource = new InputStreamResource(stream);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    // Generate CSV using built-in Java functionality
    private ByteArrayInputStream generateCsv(List<Product> products) {
        StringBuilder csvBuilder = new StringBuilder();

        // Append the header
        csvBuilder.append("ID,Name,Unit Price,Selling Price,Vendor Name,Category,Image Path\n");

        // Append each product's details
        for (Product product : products) {
            csvBuilder.append(product.getId()).append(",");
            csvBuilder.append(product.getName()).append(",");
            csvBuilder.append(product.getUnitPrice()).append(",");
            csvBuilder.append(product.getSellingPrice()).append(",");
            csvBuilder.append(product.getVendorName()).append(",");
            csvBuilder.append(product.getCategory()).append(",");  // Add category
            csvBuilder.append(product.getImagePath()).append("\n");
        }

        // Convert the CSV string into a byte stream
        return new ByteArrayInputStream(csvBuilder.toString().getBytes());
    }
}
