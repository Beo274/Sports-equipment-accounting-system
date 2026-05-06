package ru.etu.sport.parameter;

import ru.etu.sport.model.entity.Parameter;

public interface ParameterService {
    Integer create(Parameter parameter);
    void update(Integer id, Parameter parameter);

    void delete(Integer id);
}
