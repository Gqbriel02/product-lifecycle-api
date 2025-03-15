package com.example.productlifecycleapi.initializer;

import com.example.productlifecycleapi.model.Stage;
import com.example.productlifecycleapi.repository.StageRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class StageInitializer implements CommandLineRunner {

    private final StageRepository stageRepository;

    // Constant flag to enable/disable initialization:   0 - disable, 1 - enable
    private static final int INITIALIZE_STAGES = 0;

    public StageInitializer(StageRepository stageRepository) {
        this.stageRepository = stageRepository;
    }

    @Override
    public void run(String... args) {
        if (INITIALIZE_STAGES == 1) {
            List<Stage> stages = Arrays.asList(
                    new Stage(null, "Concept", "Generating ideas and defining the vision for a new product."),
                    new Stage(null, "Feasibility", "Assessing the technical, economic, and commercial viability of the proposed product."),
                    new Stage(null, "Design", "Creating technical details and specifications for product development."),
                    new Stage(null, "Production", "Manufacturing the product according to established specifications."),
                    new Stage(null, "Withdrawal", "Gradually removing the product from the market at the end of its lifecycle."),
                    new Stage(null, "Stand-by", "Temporarily suspending the product without permanently withdrawing it."),
                    new Stage(null, "Cancel", "Completely canceling the development or manufacturing of the product.")
            );

            for (Stage stage : stages) {
                if (stageRepository.findByName(stage.getName()).isEmpty()) {
                    stageRepository.save(stage);
                    System.out.println("Inserted Stage: " + stage.getName());
                }
            }
            System.out.println("Stages initialization completed.");
        } else {
            System.out.println("Stages initialization is disabled.");
        }
    }
}
