package ru.etu.sport.techroute.operations.dto;

import java.util.List;

public record TechnologyRouteNode(
        Long id,
        Long productId,
        String productName,
        Long operationNumber,
        String name,
        String operationType,
        String workCenter,
        String profession,
        String qualification,
        Long setupTime,
        Long pieceTime,
        List<TechnologyRouteInput> inputs
) {}
