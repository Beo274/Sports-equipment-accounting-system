package ru.etu.sport.parameter;

import ru.etu.sport.model.dto.request.CreateParamDto;

public interface ParameterService {
    Integer create(CreateParamDto createParamDto);

    void update(Integer id, CreateParamDto createParamDto);

    void delete(Integer id);
}
