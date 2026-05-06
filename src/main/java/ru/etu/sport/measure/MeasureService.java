package ru.etu.sport.measure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.etu.sport.model.entity.Measure;

@Service
@RequiredArgsConstructor
public class MeasureService {
    private final MeasureRepository measureRepository;

    public Measure createMeasure(Measure measure) {
        return measureRepository.save(measure);
    }
}