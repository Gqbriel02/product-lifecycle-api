package com.example.productlifecycleapi.service;

import com.example.productlifecycleapi.model.Stage;
import com.example.productlifecycleapi.repository.StageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StageService {

    private final StageRepository stageRepository;

    public StageService(StageRepository stageRepository) {
        this.stageRepository = stageRepository;
    }

    public List<Stage> getAllStages() {
        return stageRepository.findAll();
    }

    public Optional<Stage> getStageById(Long stageId) {
        return stageRepository.findById(stageId);
    }
}
