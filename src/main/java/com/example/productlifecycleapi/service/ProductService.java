package com.example.productlifecycleapi.service;

import com.example.productlifecycleapi.dto.DetailedProductResponse;
import com.example.productlifecycleapi.dto.ProductCreationRequest;
import com.example.productlifecycleapi.dto.ProductResponse;
import com.example.productlifecycleapi.dto.StageHistoryResponse;
import com.example.productlifecycleapi.model.*;
import com.example.productlifecycleapi.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final BomRepository bomRepository;
    private final MaterialRepository materialRepository;
    private final BomMaterialRepository bomMaterialRepository;
    private final StageRepository stageRepository;
    private final ProductStageHistoryRepository productStageHistoryRepository;
    private final AppUserRepository userRepository;

    public ProductService(ProductRepository productRepository,
                          BomRepository bomRepository,
                          MaterialRepository materialRepository,
                          BomMaterialRepository bomMaterialRepository,
                          StageRepository stageRepository,
                          ProductStageHistoryRepository productStageHistoryRepository,
                          AppUserRepository userRepository) {
        this.productRepository = productRepository;
        this.bomRepository = bomRepository;
        this.materialRepository = materialRepository;
        this.bomMaterialRepository = bomMaterialRepository;
        this.stageRepository = stageRepository;
        this.productStageHistoryRepository = productStageHistoryRepository;
        this.userRepository = userRepository;
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(product -> {
            Optional<ProductStageHistory> latestStage = productStageHistoryRepository.findLatestStageByProductId(product.getId());
            String stageName = latestStage.map(stage -> stage.getStage().getName()).orElse("Unknown");

            return new ProductResponse(
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getEstimatedHeight(),
                    product.getEstimatedWidth(),
                    product.getEstimatedWeight(),
                    stageName
            );
        }).collect(Collectors.toList());
    }

    public Optional<DetailedProductResponse> getProductById(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return Optional.empty();
        }

        Product product = productOpt.get();

        // Fetch latest stage
        Optional<ProductStageHistory> latestStageOpt = productStageHistoryRepository.findLatestStageByProductId(productId);
        String stageName = latestStageOpt.map(stage -> stage.getStage().getName()).orElse("Unknown");

        // Fetch materials associated with the product's BOM
        List<BomMaterial> bomMaterials = bomMaterialRepository.findByBomId(product.getBom().getId());
        List<Map<String, Object>> materials = bomMaterials.stream().map(bm -> {
            Map<String, Object> materialMap = new HashMap<>();
            materialMap.put("materialNumber", bm.getMaterial().getMaterialNumber());
            materialMap.put("materialDescription", bm.getMaterial().getMaterialDescription());
            materialMap.put("quantity", bm.getQty());
            materialMap.put("unit", bm.getUnitMeasureCode());
            return materialMap;
        }).collect(Collectors.toList());

        // Create DetailedProductResponse
        return Optional.of(new DetailedProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getEstimatedHeight(),
                product.getEstimatedWidth(),
                product.getEstimatedWeight(),
                stageName,
                materials
        ));
    }

    @Transactional
    public Product createProduct(ProductCreationRequest request, Long userId) {
        // Step 1: Fetch the User who is creating the product
        Optional<AppUser> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found.");
        }

        // Step 2: Create a BOM (Bill of Materials)
        Bom bom = new Bom();
        bom.setName(request.getName() + "_BOM");
        Bom savedBom = bomRepository.save(bom);

        // Step 3: Create the Product
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .estimatedHeight(request.getEstimatedHeight())
                .estimatedWidth(request.getEstimatedWidth())
                .estimatedWeight(request.getEstimatedWeight())
                .bom(savedBom)
                .build();

        Product savedProduct = productRepository.save(product);

        // Step 4: Assign Materials to the BOM
        for (ProductCreationRequest.MaterialSelection materialSelection : request.getMaterials()) {
            Material material = materialRepository.findById(materialSelection.getMaterialNumber())
                    .orElseThrow(() -> new RuntimeException("Material not found: " + materialSelection.getMaterialNumber()));

            BomMaterial bomMaterial = new BomMaterial();
            bomMaterial.setId(new BomMaterialId(savedBom.getId(), material.getMaterialNumber()));
            bomMaterial.setBom(savedBom);
            bomMaterial.setMaterial(material);
            bomMaterial.setQty(materialSelection.getQty());
            bomMaterial.setUnitMeasureCode(materialSelection.getUnitMeasureCode());

            bomMaterialRepository.save(bomMaterial);
        }

        // Step 5: Assign Initial Stage ("Concept") to the Product
        Optional<Stage> conceptStage = stageRepository.findByName("Concept");
        if (conceptStage.isEmpty()) {
            throw new RuntimeException("Concept stage not found in database.");
        }

        ProductStageHistory stageHistory = ProductStageHistory.builder()
                .id(new ProductStageHistoryId(conceptStage.get().getId(), savedProduct.getId(), LocalDateTime.now()))
                .stage(conceptStage.get())
                .product(savedProduct)
                .user(user.get())
                .build();

        productStageHistoryRepository.save(stageHistory);

        return savedProduct;
    }

    @Transactional
    public Product updateProduct(Long productId, ProductCreationRequest request, Long userId) {
        Optional<Product> existingProductOpt = productRepository.findById(productId);
        if (existingProductOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found.");
        }

        Product existingProduct = existingProductOpt.get();

        Optional<AppUser> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setEstimatedHeight(request.getEstimatedHeight());
        existingProduct.setEstimatedWidth(request.getEstimatedWidth());
        existingProduct.setEstimatedWeight(request.getEstimatedWeight());

        // Update Bill of Materials (BOM)
        Bom bom = existingProduct.getBom();
        List<BomMaterial> existingBomMaterials = bomMaterialRepository.findByBomId(bom.getId());

        // Convert request materials to a Map for quick lookup
        Map<String, ProductCreationRequest.MaterialSelection> requestMaterialsMap = request.getMaterials().stream()
                .collect(Collectors.toMap(ProductCreationRequest.MaterialSelection::getMaterialNumber, m -> m));

        // Update existing materials and remove ones no longer present
        existingBomMaterials.forEach(bomMaterial -> {
            String materialNumber = bomMaterial.getMaterial().getMaterialNumber();
            if (requestMaterialsMap.containsKey(materialNumber)) {
                // Update existing material's quantity and unit
                ProductCreationRequest.MaterialSelection updatedMaterial = requestMaterialsMap.get(materialNumber);
                bomMaterial.setQty(updatedMaterial.getQty());
                bomMaterial.setUnitMeasureCode(updatedMaterial.getUnitMeasureCode());
                requestMaterialsMap.remove(materialNumber); // Remove from map to avoid re-adding
            } else {
                // Material is not in the request, so remove it
                bomMaterialRepository.delete(bomMaterial);
            }
        });

        // Add new materials that were not already in the BOM
        for (ProductCreationRequest.MaterialSelection materialSelection : requestMaterialsMap.values()) {
            Material material = materialRepository.findById(materialSelection.getMaterialNumber())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Material not found: " + materialSelection.getMaterialNumber()));

            BomMaterial bomMaterial = new BomMaterial();
            bomMaterial.setId(new BomMaterialId(bom.getId(), material.getMaterialNumber()));
            bomMaterial.setBom(bom);
            bomMaterial.setMaterial(material);
            bomMaterial.setQty(materialSelection.getQty());
            bomMaterial.setUnitMeasureCode(materialSelection.getUnitMeasureCode());

            bomMaterialRepository.save(bomMaterial);
        }

        return productRepository.save(existingProduct);
    }


    public List<BomMaterial> getProductMaterials(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found.");
        }

        Product product = productOpt.get();
        Bom bom = product.getBom();

        return bomMaterialRepository.findByBomId(bom.getId());
    }

    public Optional<ProductStageHistory> getLatestProductStage(Long productId) {
        return productStageHistoryRepository.findLatestStageByProductId(productId);
    }

    @Transactional
    public void updateProductStage(Long productId, Long stageId, Long userId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found.");
        }

        Optional<Stage> stageOpt = stageRepository.findById(stageId);
        if (stageOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Stage not found.");
        }

        Optional<AppUser> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        // Add a new record in product_stage_history for the new stage
        ProductStageHistory newStageHistory = ProductStageHistory.builder()
                .id(new ProductStageHistoryId(stageId, productId, LocalDateTime.now()))
                .stage(stageOpt.get())
                .product(productOpt.get())
                .user(userOpt.get())
                .build();

        productStageHistoryRepository.save(newStageHistory);
    }


    @Transactional
    public void deleteProduct(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found.");
        }

        Product product = productOpt.get();

        // Step 1: Delete associated records in `product_stage_history`
        productStageHistoryRepository.deleteByProductId(productId);

        // Step 2: Delete BOM materials associated with this product's BOM
        Bom bom = product.getBom();
        if (bom != null) {
            bomMaterialRepository.deleteByBomId(bom.getId());

            // Step 3: Delete the BOM itself
            bomRepository.delete(bom);
        }

        // Step 4: Delete the Product
        productRepository.delete(product);
    }

    public List<StageHistoryResponse> getProductStageHistory(Long productId) {
        List<ProductStageHistory> stageHistoryList = productStageHistoryRepository.findByProductId(productId);

        return stageHistoryList.stream().map(history -> new StageHistoryResponse(
                history.getStage().getName(),
                history.getId().getStartOfStage(),
                history.getUser().getId(),
                history.getUser().getName()
        )).collect(Collectors.toList());
    }

}
