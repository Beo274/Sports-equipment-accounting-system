package ru.etu.sport.parameter;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import ru.etu.sport.model.entity.Parameter;

@Service
public class ParameterServiceImpl implements ParameterService {
    private final ParameterRepository parameterRepository;

    ParameterServiceImpl(ParameterRepository parameterRepository) {
        this.parameterRepository = parameterRepository;
    }

    @Override
    public Integer create(Parameter parameter) {
        return parameterRepository.save(parameter).getId();
    }

    @Override
    public void update(Integer id,  Parameter parameter) {
        Parameter currParam = parameterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity with id " + id + "not found"));
        currParam.setName(parameter.getName());
        currParam.setShortName(parameter.getShortName());
        currParam.setMeasure(parameter.getMeasure());
    }

    @Override
    public void delete(Integer id) {
        parameterRepository.deleteById(id);
    }
}
