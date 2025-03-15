package com.example.productlifecycleapi.util;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JasperCompiler {

    // Adjust these paths as needed
    private static final String REPORTS_DIR = "src/main/resources/reports";
    private static final String MATERIALS_SUBREPORT_JRXML = "materials_subreport.jrxml";
    private static final String STAGE_HISTORY_SUBREPORT_JRXML = "stage_history_subreport.jrxml";
    private static final String MAIN_REPORT_JRXML = "product_report.jrxml";

    public static void main(String[] args) {
        try {
            System.out.println("🚀 Compiling JasperReports...");

            // 1) Ensure the target directory exists
            createReportsDirectory();

            // 2) Compile subreports first
            compileSubreport(MATERIALS_SUBREPORT_JRXML);
            compileSubreport(STAGE_HISTORY_SUBREPORT_JRXML);

            // 3) Compile the main report (depends on subreport .jasper files)
            compileMainReport(MAIN_REPORT_JRXML);

            System.out.println("✅ All reports compiled successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Error compiling reports: " + e.getMessage());
        }
    }

    /**
     * Creates the reports directory if it does not exist.
     */
    private static void createReportsDirectory() throws Exception {
        Path reportsPath = Paths.get(REPORTS_DIR);
        if (!Files.exists(reportsPath)) {
            Files.createDirectories(reportsPath);
            System.out.println("📁 Created reports directory: " + reportsPath.toAbsolutePath());
        }
    }

    /**
     * Compiles a subreport .jrxml into a .jasper file.
     */
    private static void compileSubreport(String jrxmlFileName) throws JRException {
        String sourceFile = REPORTS_DIR + File.separator + jrxmlFileName;
        String outputFile = sourceFile.replace(".jrxml", ".jasper");

        System.out.println("🔹 Compiling subreport: " + sourceFile);
        JasperCompileManager.compileReportToFile(sourceFile, outputFile);
        System.out.println("✅ Subreport compiled: " + outputFile);
    }

    /**
     * Compiles the main report, ensuring subreport .jasper files exist beforehand.
     */
    private static void compileMainReport(String jrxmlFileName) throws JRException {
        // Ensure subreports are compiled
        checkSubreportExists(MATERIALS_SUBREPORT_JRXML.replace(".jrxml", ".jasper"));
        checkSubreportExists(STAGE_HISTORY_SUBREPORT_JRXML.replace(".jrxml", ".jasper"));

        String sourceFile = REPORTS_DIR + File.separator + jrxmlFileName;
        String outputFile = sourceFile.replace(".jrxml", ".jasper");

        System.out.println("🔹 Compiling main report: " + sourceFile);
        JasperCompileManager.compileReportToFile(sourceFile, outputFile);
        System.out.println("✅ Main report compiled: " + outputFile);
    }

    /**
     * Throws an exception if the subreport .jasper file is missing.
     */
    private static void checkSubreportExists(String jasperFileName) {
        String subreportPath = REPORTS_DIR + File.separator + jasperFileName;
        if (!new File(subreportPath).exists()) {
            throw new RuntimeException("❌ Subreport missing: " + subreportPath);
        }
    }
}
