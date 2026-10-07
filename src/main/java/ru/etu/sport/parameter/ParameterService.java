package ru.etu.sport.parameter;

import java.util.List;

import ru.etu.sport.model.dto.request.CreateParamDto;
import ru.etu.sport.model.dto.response.ParameterResponse;

public interface ParameterService {
    Integer create(CreateParamDto createParamDto);

    void update(Integer id, CreateParamDto createParamDto);

    void delete(Integer id);

    List<ParameterResponse> getAll();
}
