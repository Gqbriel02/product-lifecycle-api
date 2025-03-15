package com.example.productlifecycleapi.service;

import com.example.productlifecycleapi.model.*;
import com.example.productlifecycleapi.repository.BomMaterialRepository;
import com.example.productlifecycleapi.repository.ProductRepository;
import com.example.productlifecycleapi.repository.ProductStageHistoryRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ReportService {

    private final ProductRepository productRepository;
    private final ProductStageHistoryRepository productStageHistoryRepository;
    private final BomMaterialRepository bomMaterialRepository;

    public ReportService(ProductRepository productRepository,
                         ProductStageHistoryRepository productStageHistoryRepository,
                         BomMaterialRepository bomMaterialRepository) {
        this.productRepository = productRepository;
        this.productStageHistoryRepository = productStageHistoryRepository;
        this.bomMaterialRepository = bomMaterialRepository;
    }

    public ByteArrayInputStream generateProductReport(Long productId) throws JRException {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found");
        }

        Product product = productOpt.get();

        // Load the main JasperReport template
        InputStream reportStream = getClass().getClassLoader().getResourceAsStream("reports/product_report.jasper");
        if (reportStream == null) {
            throw new RuntimeException("Report template not found");
        }

        // Fetch the latest stage of the product
        Optional<ProductStageHistory> latestStageOpt = productStageHistoryRepository.findLatestStageByProductId(productId);
        String latestStage = latestStageOpt.map(stage -> stage.getStage().getName()).orElse("Unknown");

        // Fetch product stage history
        List<ProductStageHistory> stageHistoryList = productStageHistoryRepository.findByProductId(productId);

        // Fetch materials from BOM
        List<BomMaterial> materials = bomMaterialRepository.findByBomId(product.getBom().getId());

        // Prepare report parameters
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ProductID", product.getId());
        parameters.put("ProductName", product.getName());
        parameters.put("Description", product.getDescription());
        parameters.put("Height", product.getEstimatedHeight());
        parameters.put("Width", product.getEstimatedWidth());
        parameters.put("Weight", product.getEstimatedWeight());
        parameters.put("StageName", latestStage);


        // Load subreports
        InputStream materialsSubreportStream = getClass().getResourceAsStream("/reports/materials_subreport.jasper");
        InputStream stageHistorySubreportStream = getClass().getResourceAsStream("/reports/stage_history_subreport.jasper");

        if (materialsSubreportStream == null || stageHistorySubreportStream == null) {
            throw new RuntimeException("Subreport templates not found");
        }

        parameters.put("MaterialsSubreport", materialsSubreportStream);
        parameters.put("StageHistorySubreport", stageHistorySubreportStream);

        // Data sources
        JRBeanCollectionDataSource materialsDataSource = new JRBeanCollectionDataSource(materials);
        JRBeanCollectionDataSource stageHistoryDataSource = new JRBeanCollectionDataSource(stageHistoryList);

        parameters.put("MaterialsDataSource", materialsDataSource);
        parameters.put("StageHistoryDataSource", stageHistoryDataSource);

        // Fill the report
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, new JREmptyDataSource());

        // Export to PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}
