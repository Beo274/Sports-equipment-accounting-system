package ru.etu.sport.parameter;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.stereotype.Service;

import ru.etu.sport.measure.MeasureRepository;
import ru.etu.sport.model.dto.request.CreateParamDto;
import ru.etu.sport.model.dto.response.ParameterResponse;
import ru.etu.sport.model.entity.Measure;
import ru.etu.sport.model.entity.Parameter;

@Service
public class ParameterServiceImpl implements ParameterService {
    private final ParameterRepository parameterRepository;
    private final MeasureRepository measureRepository;

    ParameterServiceImpl(ParameterRepository parameterRepository, MeasureRepository measureRepository) {
        this.parameterRepository = parameterRepository;
        this.measureRepository = measureRepository;
    }

    @Override
    public Integer create(CreateParamDto createParamDto) {
        Parameter parameter = new Parameter();

        parameter.setName(createParamDto.getName());
        parameter.setShortName(createParamDto.getShortName());

        if (createParamDto.getMeasureId() != null) {
             Measure measureRef = this.measureRepository.getReferenceById(createParamDto.getMeasureId());
            if (measureRef != null) {
                parameter.setMeasure(measureRef);
            }
        }
       
        return parameterRepository.save(parameter).getId();
    }

    @Transactional
    @Override
    public void update(Integer id,  CreateParamDto createParamDto) {
        Parameter currParam = parameterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity with id " + id + "not found"));
        currParam.setName(createParamDto.getName());
        currParam.setShortName(createParamDto.getShortName());

        if (createParamDto.getMeasureId() != null) {
            Measure measure = this.measureRepository.getReferenceById(createParamDto.getMeasureId());
            currParam.setMeasure(measure);
        }
    }

    @Override
    public void delete(Integer id) {
        parameterRepository.deleteById(id);
    }

    @Override
    public List<ParameterResponse> getAll() {
        return this.parameterRepository.findAll().stream()
            .map(p -> ParameterResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .shortName(p.getShortName())
                .measure(p.getMeasure())
                .build())
            .toList();
    }
}
