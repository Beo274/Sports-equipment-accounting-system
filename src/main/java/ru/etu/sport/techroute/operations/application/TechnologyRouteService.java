package ru.etu.sport.techroute.operations.application;

import java.util.List;

import ru.etu.sport.techroute.operations.dto.CreateOperationInputDto;
import ru.etu.sport.techroute.operations.dto.OperationInputResponse;
import ru.etu.sport.techroute.operations.dto.TechnologyRouteResponse;

public interface TechnologyRouteService {
    OperationInputResponse addInput(Long operationInput, CreateOperationInputDto dto);

    List<OperationInputResponse> getInputs(Long operationId);

    OperationInputResponse updateInput(Long operationId, Long inputId, CreateOperationInputDto dto);

    void deleteInput(Long operationId, Long inputId);

    TechnologyRouteResponse getFullRoute(Long productId);
}