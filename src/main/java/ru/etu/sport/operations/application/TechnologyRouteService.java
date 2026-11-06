package ru.etu.sport.operations.application;

import ru.etu.sport.operations.dto.TechnologyRouteResponse;

public interface TechnologyRouteService {

    TechnologyRouteResponse getFullRoute(Long productId);
}