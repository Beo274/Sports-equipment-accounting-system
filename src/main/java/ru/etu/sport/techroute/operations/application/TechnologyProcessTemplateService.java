package ru.etu.sport.techroute.operations.application;

import java.util.List;

import ru.etu.sport.techroute.operations.dto.CreateTechnologyProcessTemplateDto;
import ru.etu.sport.techroute.operations.dto.ProductOperationReponse;
import ru.etu.sport.techroute.operations.dto.TechnologyProcessTemplateResponse;
import ru.etu.sport.techroute.operations.dto.TechnologyProcessTemplateShortResponse;

public interface TechnologyProcessTemplateService {

    TechnologyProcessTemplateResponse createFromProduct(Long productId, CreateTechnologyProcessTemplateDto dto);

    List<TechnologyProcessTemplateShortResponse> getAll();

    TechnologyProcessTemplateResponse getById(Long templateId);

    TechnologyProcessTemplateResponse update(Long templateId, CreateTechnologyProcessTemplateDto dto);

    void delete(Long templateId);

    List<ProductOperationReponse> applyToProduct(Long productId, Long templateId);
}