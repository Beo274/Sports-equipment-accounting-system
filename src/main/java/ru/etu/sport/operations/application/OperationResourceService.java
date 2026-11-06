package ru.etu.sport.operations.application;

import java.util.List;

import ru.etu.sport.operations.dto.OperationResourceDto;
import ru.etu.sport.operations.dto.OperationResourceResponse;
import ru.etu.sport.operations.dto.UpdateOperationResourceDto;

public interface OperationResourceService {

    OperationResourceResponse create(OperationResourceDto dto);

    OperationResourceResponse update(
            Long id,
            UpdateOperationResourceDto dto
    );

    OperationResourceResponse getById(Long id);

    List<OperationResourceResponse> getByOperationId(Long operationId);

    void delete(Long id);
}