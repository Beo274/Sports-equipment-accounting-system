package ru.etu.sport.measure;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;
import ru.etu.sport.model.entity.Measure;

@Service
@RequiredArgsConstructor
public class MeasureService {
    private final MeasureRepository measureRepository;

    public Measure createMeasure(Measure measure) {
        return measureRepository.save(measure);
    }

    public List<Measure> getAll() {
        return measureRepository.findAll();
    }
}