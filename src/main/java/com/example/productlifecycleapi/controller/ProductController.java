package com.example.productlifecycleapi.controller;

import com.example.productlifecycleapi.dto.DetailedProductResponse;
import com.example.productlifecycleapi.dto.ProductCreationRequest;
import com.example.productlifecycleapi.dto.ProductResponse;
import com.example.productlifecycleapi.dto.StageHistoryResponse;
import com.example.productlifecycleapi.model.AppUser;
import com.example.productlifecycleapi.model.BomMaterial;
import com.example.productlifecycleapi.model.Product;
import com.example.productlifecycleapi.model.ProductStageHistory;
import com.example.productlifecycleapi.repository.AppUserRepository;
import com.example.productlifecycleapi.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final AppUserRepository userRepository;

    public ProductController(ProductService productService, AppUserRepository userRepository) {
        this.productService = productService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductCreationRequest request,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Optional<AppUser> user = userRepository.findByUsername(userDetails.getUsername());
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        Set<String> userRoles = user.get().getRoles().stream()
                .map(role -> role.getRoleName())
                .collect(Collectors.toSet());

        Set<String> allowedRoles = Set.of("Admin", "Designer", "Engineer", "PO");

        boolean hasPermission = userRoles.stream().anyMatch(allowedRoles::contains);
        if (!hasPermission) {
            return ResponseEntity.status(403).body("Forbidden: You do not have permission to create a product.");
        }

        Product createdProduct = productService.createProduct(request, user.get().getId());
        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId,
                                           @RequestBody ProductCreationRequest request,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Optional<AppUser> user = userRepository.findByUsername(userDetails.getUsername());
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        Set<String> userRoles = user.get().getRoles().stream()
                .map(role -> role.getRoleName())
                .collect(Collectors.toSet());

        Set<String> allowedRoles = Set.of("Admin", "Designer", "Engineer", "PO");

        boolean hasPermission = userRoles.stream().anyMatch(allowedRoles::contains);
        if (!hasPermission) {
            return ResponseEntity.status(403).body("Forbidden: You do not have permission to update a product.");
        }

        try {
            Product updatedProduct = productService.updateProduct(productId, request, user.get().getId());
            return ResponseEntity.ok(updatedProduct);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable Long productId) {
        Optional<DetailedProductResponse> detailedProductResponse = productService.getProductById(productId);
        if (detailedProductResponse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        return ResponseEntity.ok(detailedProductResponse.get());
    }

    @GetMapping("/{productId}/materials")
    public ResponseEntity<?> getProductMaterials(@PathVariable Long productId) {
        List<BomMaterial> materials = productService.getProductMaterials(productId);
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/{productId}/stage")
    public ResponseEntity<?> getProductStage(@PathVariable Long productId) {
        Optional<ProductStageHistory> latestStage = productService.getLatestProductStage(productId);
        if (latestStage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stage history not found");
        }
        return ResponseEntity.ok(latestStage.get().getStage().getName());
    }

    @PutMapping("/{productId}/stage")
    public ResponseEntity<?> updateProductStage(@PathVariable Long productId, @RequestParam Long stageId,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Optional<AppUser> user = userRepository.findByUsername(userDetails.getUsername());
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        try {
            productService.updateProductStage(productId, stageId, user.get().getId());
            return ResponseEntity.ok("Stage updated successfully");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Optional<AppUser> user = userRepository.findByUsername(userDetails.getUsername());
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        Set<String> userRoles = user.get().getRoles().stream()
                .map(role -> role.getRoleName())
                .collect(Collectors.toSet());

        Set<String> allowedRoles = Set.of("Admin", "PO");

        boolean hasPermission = userRoles.stream().anyMatch(allowedRoles::contains);
        if (!hasPermission) {
            return ResponseEntity.status(403).body("Forbidden: You do not have permission to delete a product.");
        }

        try {
            productService.deleteProduct(productId);
            return ResponseEntity.ok("Product deleted successfully.");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @GetMapping("/{productId}/stage-history")
    public ResponseEntity<List<StageHistoryResponse>> getProductStageHistory(@PathVariable Long productId) {
        List<StageHistoryResponse> stageHistory = productService.getProductStageHistory(productId);
        return ResponseEntity.ok(stageHistory);
    }
}
