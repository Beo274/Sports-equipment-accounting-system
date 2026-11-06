package ru.etu.sport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import ru.etu.sport.model.entity.ClassEntity;
import ru.etu.sport.model.entity.Measure;
import ru.etu.sport.model.entity.Product;
import ru.etu.sport.operations.adapters.OperationResourceController;
import ru.etu.sport.operations.adapters.TechnologyRouteController;
import ru.etu.sport.operations.dto.OperationResourceDto;
import ru.etu.sport.operations.dto.OperationResourceResponse;
import ru.etu.sport.operations.dto.ResourceType;
import ru.etu.sport.operations.dto.TechnologyRouteResponse;
import ru.etu.sport.operations.dto.UpdateOperationResourceDto;
import ru.etu.sport.operations.infrastructure.repository.OperationResourceRepository;
import ru.etu.sport.operations.infrastructure.repository.ProductOperationRepository;
import ru.etu.sport.operations.infrastructure.services.OperationResourceServiceImpl;
import ru.etu.sport.operations.infrastructure.services.TechnologyRouteServiceImpl;
import ru.etu.sport.operations.persistence.OperationResource;
import ru.etu.sport.operations.persistence.OperationType;
import ru.etu.sport.operations.persistence.ProductOperation;
import ru.etu.sport.operations.persistence.Profession;
import ru.etu.sport.operations.persistence.Qualification;
import ru.etu.sport.operations.persistence.WorkCenter;
import ru.etu.sport.product.ProductRepository;

@ExtendWith(MockitoExtension.class)
class OperationResourceAndRouteTest {

    @Mock
    private OperationResourceRepository operationResourceRepository;

    @Mock
    private ProductOperationRepository productOperationRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private OperationResourceServiceImpl operationResourceService;

    @InjectMocks
    private TechnologyRouteServiceImpl technologyRouteService;

    private OperationResourceController operationResourceController;
    private TechnologyRouteController technologyRouteController;

    private Product mainProduct;
    private Product resourceMaterial;
    private Product resourceTool;
    private Measure measureUnit;
    private ProductOperation operation10;
    private ProductOperation operation20;

    @BeforeEach
    void setUp() {
        operationResourceController = new OperationResourceController(operationResourceService);
        technologyRouteController = new TechnologyRouteController(technologyRouteService);

        measureUnit = new Measure();
        measureUnit.setId(1);
        measureUnit.setName("Штука");
        measureUnit.setShortName("шт");

        ClassEntity productClass = new ClassEntity();
        productClass.setId(1);
        productClass.setName("Спортинвентарь");
        productClass.setShortName("Спорт");

        mainProduct = new Product();
        mainProduct.setId(1);
        mainProduct.setName("Горные лыжи");
        mainProduct.setShortName("Лыжи");
        mainProduct.setProductClass(productClass);

        resourceMaterial = new Product();
        resourceMaterial.setId(2);
        resourceMaterial.setName("Карбоновое волокно");
        resourceMaterial.setShortName("Карбон");
        resourceMaterial.setProductClass(productClass);

        resourceTool = new Product();
        resourceTool.setId(3);
        resourceTool.setName("Фреза торцевая");
        resourceTool.setShortName("Фреза");
        resourceTool.setProductClass(productClass);

        OperationType opType = OperationType.builder().id(1L).name("Мехобработка").description("").build();
        WorkCenter workCenter = WorkCenter.builder().id(1L).name("Цех №1").description("").build();
        Profession profession = Profession.builder().id(1L).name("Оператор ЧПУ").description("").build();
        Qualification qualification = Qualification.builder().id(1L).name("5 разряд").description("").build();

        operation10 = ProductOperation.builder()
                .id(100L)
                .product(mainProduct)
                .operationNumber(10L)
                .name("Раскрой заготовки")
                .operationType(opType)
                .workCenter(workCenter)
                .profession(profession)
                .qualification(qualification)
                .setupTime(1500L)
                .pieceTime(500L)
                .description("Первая операция")
                .build();

        operation20 = ProductOperation.builder()
                .id(200L)
                .product(mainProduct)
                .operationNumber(20L)
                .name("Фрезеровка профиля")
                .operationType(opType)
                .workCenter(workCenter)
                .profession(profession)
                .qualification(qualification)
                .setupTime(2500L)
                .pieceTime(1000L)
                .description("Вторая операция")
                .build();
    }

    @Test
    @DisplayName("1. Создание ресурса операции (POST /api/operation-resources)")
    void testCreateOperationResource() {
        OperationResourceDto dto = new OperationResourceDto(
                100L, 2L, ResourceType.MATERIAL, 4L, 1L, 1L
        );

        when(productOperationRepository.findById(100L)).thenReturn(Optional.of(operation10));
        when(productRepository.findById(2L)).thenReturn(Optional.of(resourceMaterial));
        when(entityManager.find(Measure.class, 1)).thenReturn(measureUnit);
        when(operationResourceRepository.save(any(OperationResource.class))).thenAnswer(inv -> {
            OperationResource saved = inv.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        ResponseEntity<OperationResourceResponse> response = operationResourceController.create(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().id());
        assertEquals(100L, response.getBody().productOperationId());
        assertEquals("Карбоновое волокно", response.getBody().resource().getName());
        assertEquals(ResourceType.MATERIAL, response.getBody().resourceType());
        assertEquals(4L, response.getBody().quantity());
        assertEquals(1L, response.getBody().baseQuantity());
        assertEquals("шт", response.getBody().unit().shortName());
    }

    @Test
    @DisplayName("2. Ошибка валидации при создании ресурса с quantity <= 0")
    void testCreateValidationFailure() {
        OperationResourceDto invalidDto = new OperationResourceDto(
                100L, 2L, ResourceType.MATERIAL, 0L, 1L, 1L
        );

        assertThrows(ValidationException.class, () -> operationResourceService.create(invalidDto));
    }

    @Test
    @DisplayName("3. Получение ресурса по ID и проверка ошибки 404")
    void testGetByIdAndNotFound() {
        OperationResource entity = OperationResource.builder()
                .id(10L)
                .productOperation(operation10)
                .resource(resourceMaterial)
                .resourceType(ResourceType.MATERIAL)
                .quantity(4L)
                .baseQuantity(1L)
                .unit(measureUnit)
                .build();

        when(operationResourceRepository.findById(10L)).thenReturn(Optional.of(entity));
        when(operationResourceRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<OperationResourceResponse> response = operationResourceController.getById(10L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10L, response.getBody().id());

        assertThrows(EntityNotFoundException.class, () -> operationResourceService.getById(999L));
    }

    @Test
    @DisplayName("4. Получение списка ресурсов операции (GET /api/operation-resources/operation/{operationId})")
    void testGetByOperationId() {
        OperationResource res1 = OperationResource.builder()
                .id(10L)
                .productOperation(operation10)
                .resource(resourceMaterial)
                .resourceType(ResourceType.MATERIAL)
                .quantity(4L)
                .baseQuantity(1L)
                .unit(measureUnit)
                .build();

        OperationResource res2 = OperationResource.builder()
                .id(11L)
                .productOperation(operation10)
                .resource(resourceTool)
                .resourceType(ResourceType.TOOL)
                .quantity(1L)
                .baseQuantity(10L)
                .unit(measureUnit)
                .build();

        when(productOperationRepository.existsById(100L)).thenReturn(true);
        when(operationResourceRepository.findAllByProductOperationId(100L)).thenReturn(List.of(res1, res2));

        ResponseEntity<List<OperationResourceResponse>> response =
                operationResourceController.getByOperationId(100L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(ResourceType.MATERIAL, response.getBody().get(0).resourceType());
        assertEquals(ResourceType.TOOL, response.getBody().get(1).resourceType());
    }

    @Test
    @DisplayName("5. Частичное обновление привязки ресурса (PATCH /api/operation-resources/{id})")
    void testUpdateOperationResource() {
        OperationResource existing = OperationResource.builder()
                .id(10L)
                .productOperation(operation10)
                .resource(resourceMaterial)
                .resourceType(ResourceType.MATERIAL)
                .quantity(4L)
                .baseQuantity(1L)
                .unit(measureUnit)
                .build();

        UpdateOperationResourceDto updateDto = new UpdateOperationResourceDto(
                null, null, ResourceType.FIXTURE, 8L, 2L, null
        );

        when(operationResourceRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(operationResourceRepository.save(any(OperationResource.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<OperationResourceResponse> response =
                operationResourceController.update(10L, updateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResourceType.FIXTURE, response.getBody().resourceType());
        assertEquals(8L, response.getBody().quantity());
        assertEquals(2L, response.getBody().baseQuantity());
    }

    @Test
    @DisplayName("6. Удаление привязки ресурса (DELETE /api/operation-resources/{id})")
    void testDeleteOperationResource() {
        when(operationResourceRepository.existsById(10L)).thenReturn(true);

        ResponseEntity<Void> response = operationResourceController.delete(10L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(operationResourceRepository).deleteById(10L);
    }

    @Test
    @DisplayName("7. Получение полного технологического маршрута изделия (getFullRoute)")
    void testGetFullRoute() {
        OperationResource resOp10 = OperationResource.builder()
                .id(10L)
                .productOperation(operation10)
                .resource(resourceMaterial)
                .resourceType(ResourceType.MATERIAL)
                .quantity(4L)
                .baseQuantity(1L)
                .unit(measureUnit)
                .build();

        OperationResource resOp20 = OperationResource.builder()
                .id(11L)
                .productOperation(operation20)
                .resource(resourceTool)
                .resourceType(ResourceType.TOOL)
                .quantity(1L)
                .baseQuantity(5L)
                .unit(measureUnit)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(mainProduct));
        when(productOperationRepository.findAllByProduct(mainProduct))
                .thenReturn(new ArrayList<>(List.of(operation20, operation10)));
        when(operationResourceRepository.findAllByProductOperationIdIn(List.of(100L, 200L)))
                .thenReturn(List.of(resOp10, resOp20));

        ResponseEntity<TechnologyRouteResponse> response = technologyRouteController.getFullRoute(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TechnologyRouteResponse route = response.getBody();
        assertNotNull(route);

        assertEquals("Горные лыжи", route.product().getName());
        assertEquals(4000L, route.totalSetupTime());
        assertEquals(1500L, route.totalPieceTime());

        assertEquals(2, route.operations().size());
        assertEquals(10L, route.operations().get(0).operationNumber());
        assertEquals(20L, route.operations().get(1).operationNumber());

        assertEquals(1, route.operations().get(0).resources().size());
        assertEquals("Карбоновое волокно", route.operations().get(0).resources().get(0).resource().getName());

        assertEquals(1, route.operations().get(1).resources().size());
        assertEquals("Фреза торцевая", route.operations().get(1).resources().get(0).resource().getName());
    }
}