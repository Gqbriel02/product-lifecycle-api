package com.example.productlifecycleapi.initializer;

import com.example.productlifecycleapi.model.Material;
import com.example.productlifecycleapi.repository.MaterialRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class MaterialInitializer implements CommandLineRunner {

    private final MaterialRepository materialRepository;

    // Constant flag to enable/disable initialization:   0 - disable, 1 - enable
    private static final int INITIALIZE_MATERIALS = 0;

    public MaterialInitializer(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @Override
    public void run(String... args) {
        if (INITIALIZE_MATERIALS == 1) {
            List<Material> materials = Arrays.asList(
                    new Material("M001", "Galvanized Steel", 5000.0, 200.0, 150.0),
                    new Material("M002", "Tempered Glass", 3000.0, 150.0, 100.0),
                    new Material("M003", "ABS Plastic", 1500.0, 120.0, 90.0),
                    new Material("M004", "Copper Wire", 1000.0, 0.5, 1000.0),
                    new Material("M005", "Polycrystalline Silicon", 2500.0, 50.0, 50.0),
                    new Material("M006", "Carbon Fiber Sheet", 800.0, 100.0, 200.0),
                    new Material("M007", "Aluminum Alloy Panel", 1200.0, 250.0, 180.0),
                    new Material("M008", "Lithium-ion Battery", 600.0, 50.0, 80.0),
                    new Material("M009", "PCB (Printed Circuit Board)", 300.0, 30.0, 50.0),
                    new Material("M010", "LED Chipset", 50.0, 5.0, 10.0),
                    new Material("M011", "Titanium Alloy", 4500.0, 180.0, 140.0),
                    new Material("M012", "Stainless Steel", 5200.0, 210.0, 160.0),
                    new Material("M013", "Borosilicate Glass", 2800.0, 140.0, 110.0),
                    new Material("M014", "Kevlar Fiber", 700.0, 90.0, 130.0),
                    new Material("M015", "Graphene Sheet", 10.0, 50.0, 50.0),
                    new Material("M016", "Silicon Carbide", 3200.0, 100.0, 80.0),
                    new Material("M017", "Nickel Foam", 600.0, 75.0, 60.0),
                    new Material("M018", "Rubber Composite", 900.0, 120.0, 100.0),
                    new Material("M019", "Neodymium Magnet", 1200.0, 50.0, 30.0),
                    new Material("M020", "Tungsten Carbide", 6400.0, 160.0, 140.0),
                    new Material("M021", "Zinc Coated Steel", 4800.0, 200.0, 150.0),
                    new Material("M022", "Pyrex Glass", 2700.0, 130.0, 100.0),
                    new Material("M023", "Acrylic Sheet", 1100.0, 100.0, 80.0),
                    new Material("M024", "High-Density Polyethylene", 800.0, 120.0, 90.0),
                    new Material("M025", "Beryllium Copper", 3900.0, 80.0, 150.0),
                    new Material("M026", "Fiberglass", 2000.0, 140.0, 100.0),
                    new Material("M027", "Electrum Alloy", 15000.0, 50.0, 50.0),
                    new Material("M028", "Synthetic Diamond", 3500.0, 20.0, 10.0),
                    new Material("M029", "Vanadium Steel", 5100.0, 180.0, 130.0),
                    new Material("M030", "Alumina Ceramic", 2900.0, 120.0, 110.0),
                    new Material("M031", "Polyvinyl Chloride (PVC)", 1400.0, 130.0, 90.0),
                    new Material("M032", "Carbon Nanotube Fabric", 50.0, 30.0, 10.0),
                    new Material("M033", "Bismuth Telluride", 1800.0, 80.0, 70.0),
                    new Material("M034", "Cobalt-Chromium Alloy", 7500.0, 100.0, 90.0),
                    new Material("M035", "Phosphor Bronze", 4200.0, 90.0, 100.0),
                    new Material("M036", "Rare Earth Metal Oxide", 2600.0, 50.0, 50.0),
                    new Material("M037", "High-Strength Concrete", 5000.0, 250.0, 200.0),
                    new Material("M038", "Transparent Aluminum (ALON)", 3300.0, 70.0, 60.0),
                    new Material("M039", "Superalloy Inconel", 8400.0, 120.0, 110.0),
                    new Material("M040", "Shape Memory Alloy (Nitinol)", 6000.0, 75.0, 80.0),
                    new Material("M041", "Gallium Arsenide", 5400.0, 40.0, 30.0),
                    new Material("M042", "Silicone Elastomer", 1100.0, 100.0, 80.0),
                    new Material("M043", "Borated Polyethylene", 1800.0, 130.0, 100.0),
                    new Material("M044", "Yttrium Barium Copper Oxide", 3200.0, 60.0, 50.0),
                    new Material("M045", "Aerogel Insulation", 50.0, 200.0, 150.0),
                    new Material("M046", "Expanded Polystyrene", 500.0, 140.0, 110.0),
                    new Material("M047", "Ferrofluid", 1000.0, 30.0, 50.0),
                    new Material("M048", "Thermoplastic Polyurethane", 1200.0, 120.0, 90.0),
                    new Material("M049", "Bamboo Fiber Composite", 800.0, 200.0, 180.0),
                    new Material("M050", "Conductive Ink", 150.0, 20.0, 10.0)
            );

            for (Material material : materials) {
                if (!materialRepository.existsById(material.getMaterialNumber())) {
                    materialRepository.save(material);
                    System.out.println("Inserted Material: " + material.getMaterialDescription());
                }
            }
            System.out.println("Material initialization completed.");
        } else {
            System.out.println("Material initialization is disabled.");
        }
    }
}
