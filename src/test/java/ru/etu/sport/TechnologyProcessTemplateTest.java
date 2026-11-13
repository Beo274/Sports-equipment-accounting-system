package ru.etu.sport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.persistence.EntityNotFoundException;
import ru.etu.sport.model.entity.ClassEntity;
import ru.etu.sport.model.entity.Measure;
import ru.etu.sport.model.entity.Product;
import ru.etu.sport.product.ProductRepository;
import ru.etu.sport.techroute.operations.adapters.TechnologyProcessTemplateApplicationController;
import ru.etu.sport.techroute.operations.adapters.TechnologyProcessTemplateController;
import ru.etu.sport.techroute.operations.domain.OperationKind;
import ru.etu.sport.techroute.operations.dto.CreateTechnologyProcessTemplateDto;
import ru.etu.sport.techroute.operations.dto.ProductOperationReponse;
import ru.etu.sport.techroute.operations.dto.TechnologyProcessTemplateResponse;
import ru.etu.sport.techroute.operations.dto.TechnologyProcessTemplateShortResponse;
import ru.etu.sport.techroute.operations.infrastructure.repository.OperationInputRepository;
import ru.etu.sport.techroute.operations.infrastructure.repository.ProductOperationRepository;
import ru.etu.sport.techroute.operations.infrastructure.repository.TechnologyProcessTemplateInputRepository;
import ru.etu.sport.techroute.operations.infrastructure.repository.TechnologyProcessTemplateOperationRepository;
import ru.etu.sport.techroute.operations.infrastructure.repository.TechnologyProcessTemplateRepository;
import ru.etu.sport.techroute.operations.infrastructure.services.TechnologyProcessTemplateServiceImpl;
import ru.etu.sport.techroute.operations.persistence.OperationInput;
import ru.etu.sport.techroute.operations.persistence.OperationType;
import ru.etu.sport.techroute.operations.persistence.ProductOperation;
import ru.etu.sport.techroute.operations.persistence.Profession;
import ru.etu.sport.techroute.operations.persistence.Qualification;
import ru.etu.sport.techroute.operations.persistence.TechnologyProcessTemplate;
import ru.etu.sport.techroute.operations.persistence.TechnologyProcessTemplateInput;
import ru.etu.sport.techroute.operations.persistence.TechnologyProcessTemplateOperation;
import ru.etu.sport.techroute.operations.persistence.WorkCenter;

@ExtendWith(MockitoExtension.class)
class TechnologyProcessTemplateTest {

    @Mock
    private TechnologyProcessTemplateRepository templateRepository;

    @Mock
    private TechnologyProcessTemplateOperationRepository templateOperationRepository;

    @Mock
    private TechnologyProcessTemplateInputRepository templateInputRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductOperationRepository productOperationRepository;

    @Mock
    private OperationInputRepository operationInputRepository;

    @InjectMocks
    private TechnologyProcessTemplateServiceImpl templateService;

    private TechnologyProcessTemplateController templateController;
    private TechnologyProcessTemplateApplicationController applicationController;

    private Product productFrameA;
    private Product productFrameB;
    private Measure measureUnit;
    private ProductOperation prodOp1;
    private ProductOperation prodOp2;
    private ProductOperation prodOp3;
    private OperationInput opInput1to2;
    private OperationInput opInput2to3;

    @BeforeEach
    void setUp() {
        templateController = new TechnologyProcessTemplateController(templateService);
        applicationController = new TechnologyProcessTemplateApplicationController(templateService);

        measureUnit = new Measure();
        measureUnit.setId(1);
        measureUnit.setName("Штука");
        measureUnit.setShortName("шт");

        ClassEntity productClass = new ClassEntity();
        productClass.setId(1);
        productClass.setName("Рамы");
        productClass.setShortName("РМ");

        productFrameA = new Product();
        productFrameA.setId(1);
        productFrameA.setName("Деревянная рама");
        productFrameA.setShortName("ДР-А");
        productFrameA.setProductClass(productClass);

        productFrameB = new Product();
        productFrameB.setId(2);
        productFrameB.setName("Деревянная рама модели Б");
        productFrameB.setShortName("ДР-Б");
        productFrameB.setProductClass(productClass);

        OperationType opType = OperationType.builder().id(1L).name("Деревообработка").description("").build();
        WorkCenter workCenter = WorkCenter.builder().id(1L).name("Столярный цех").description("").build();
        Profession profession = Profession.builder().id(1L).name("Столяр").description("").build();
        Qualification qualification = Qualification.builder().id(1L).name("4 разряд").description("").build();

        prodOp1 = ProductOperation.builder()
                .id(101L)
                .product(productFrameA)
                .operationNumber(1L)
                .name("Распиливание бруса")
                .kind(OperationKind.TECHNOLOGICAL)
                .setupTime(10L)
                .pieceTime(20L)
                .operationType(opType)
                .workCenter(workCenter)
                .profession(profession)
                .qualification(qualification)
                .description("Операция 1")
                .build();

        prodOp2 = ProductOperation.builder()
                .id(102L)
                .product(productFrameA)
                .operationNumber(2L)
                .name("Обработка досок")
                .kind(OperationKind.TECHNOLOGICAL)
                .setupTime(5L)
                .pieceTime(15L)
                .operationType(opType)
                .workCenter(workCenter)
                .profession(profession)
                .qualification(qualification)
                .description("Операция 2")
                .build();

        prodOp3 = ProductOperation.builder()
                .id(103L)
                .product(productFrameA)
                .operationNumber(3L)
                .name("Сборка рамы")
                .kind(OperationKind.TECHNOLOGICAL)
                .setupTime(8L)
                .pieceTime(30L)
                .operationType(opType)
                .workCenter(workCenter)
                .profession(profession)
                .qualification(qualification)
                .description("Операция 3")
                .build();

        opInput1to2 = OperationInput.builder()
                .id(501L)
                .outputOperation(prodOp2)
                .inputOperation(prodOp1)
                .quantity(BigDecimal.valueOf(4))
                .baseQuantity(BigDecimal.ONE)
                .measure(measureUnit)
                .build();

        opInput2to3 = OperationInput.builder()
                .id(502L)
                .outputOperation(prodOp3)
                .inputOperation(prodOp2)
                .quantity(BigDecimal.valueOf(4))
                .baseQuantity(BigDecimal.ONE)
                .measure(measureUnit)
                .build();
    }

    @Test
    @DisplayName("1. POST /technology-process-templates/from-product/{productId} — создание шаблона из маршрута изделия")
    void testCreateTemplateFromProduct() {
        CreateTechnologyProcessTemplateDto dto = new CreateTechnologyProcessTemplateDto(
                "Изготовление деревянной рамы",
                "Типовой процесс изготовления рамы"
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(productFrameA));
        when(productOperationRepository.findAllByProduct(productFrameA))
                .thenReturn(new ArrayList<>(List.of(prodOp3, prodOp1, prodOp2)));

        when(templateRepository.save(any(TechnologyProcessTemplate.class))).thenAnswer(inv -> {
            TechnologyProcessTemplate t = inv.getArgument(0);
            t.setId(10L);
            return t;
        });

        AtomicLong templateOpIdSeq = new AtomicLong(200L);
        when(templateOperationRepository.save(any(TechnologyProcessTemplateOperation.class))).thenAnswer(inv -> {
            TechnologyProcessTemplateOperation op = inv.getArgument(0);
            op.setId(templateOpIdSeq.incrementAndGet());
            return op;
        });

        when(operationInputRepository.findByOutputOperation_IdOrderByIdAsc(101L)).thenReturn(List.of());
        when(operationInputRepository.findByOutputOperation_IdOrderByIdAsc(102L)).thenReturn(List.of(opInput1to2));
        when(operationInputRepository.findByOutputOperation_IdOrderByIdAsc(103L)).thenReturn(List.of(opInput2to3));

        List<TechnologyProcessTemplateInput> savedTemplateInputs = new ArrayList<>();
        AtomicLong templateInputIdSeq = new AtomicLong(600L);
        when(templateInputRepository.save(any(TechnologyProcessTemplateInput.class))).thenAnswer(inv -> {
            TechnologyProcessTemplateInput ti = inv.getArgument(0);
            ti.setId(templateInputIdSeq.incrementAndGet());
            savedTemplateInputs.add(ti);
            return ti;
        });

        when(templateInputRepository.findAllByOutputTemplateOperation_IdInOrderByIdAsc(anyCollection()))
                .thenAnswer(inv -> savedTemplateInputs);

        ResponseEntity<TechnologyProcessTemplateResponse> response =
                templateController.createFromProduct(1L, dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        TechnologyProcessTemplateResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(10L, body.id());
        assertEquals("Изготовление деревянной рамы", body.name());
        assertEquals(3, body.operations().size());

        assertEquals("Распиливание бруса", body.operations().get(0).name());
        assertEquals(1, body.operations().get(0).operationNumber());

        assertEquals("Обработка досок", body.operations().get(1).name());
        assertEquals(1, body.operations().get(1).inputs().size());
        assertEquals("Распиливание бруса", body.operations().get(1).inputs().get(0).inputOperationName());

        assertEquals("Сборка рамы", body.operations().get(2).name());
        assertEquals(1, body.operations().get(2).inputs().size());
        assertEquals("Обработка досок", body.operations().get(2).inputs().get(0).inputOperationName());
    }

    @Test
    @DisplayName("2. GET /technology-process-templates — получение списка шаблонов")
    void testGetAllTemplates() {
        TechnologyProcessTemplate t1 = TechnologyProcessTemplate.builder()
                .id(10L)
                .name("Изготовление деревянной рамы")
                .description("Описание 1")
                .build();

        when(templateRepository.findAll()).thenReturn(List.of(t1));

        ResponseEntity<List<TechnologyProcessTemplateShortResponse>> response = templateController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Изготовление деревянной рамы", response.getBody().get(0).name());
    }

    @Test
    @DisplayName("3. GET /technology-process-templates/{templateId} — получение шаблона со всеми операциями")
    void testGetTemplateById() {
        TechnologyProcessTemplate template = TechnologyProcessTemplate.builder()
                .id(10L)
                .name("Изготовление деревянной рамы")
                .description("Описание")
                .build();

        TechnologyProcessTemplateOperation tOp1 = TechnologyProcessTemplateOperation.builder()
                .id(201L)
                .template(template)
                .operationNumber(1)
                .name("Распиливание бруса")
                .kind(OperationKind.TECHNOLOGICAL)
                .setupTime(10L)
                .pieceTime(20L)
                .build();

        when(templateRepository.findById(10L)).thenReturn(Optional.of(template));
        when(templateOperationRepository.findAllByTemplate_IdOrderByOperationNumberAsc(10L))
                .thenReturn(List.of(tOp1));
        when(templateInputRepository.findAllByOutputTemplateOperation_IdInOrderByIdAsc(List.of(201L)))
                .thenReturn(List.of());

        ResponseEntity<TechnologyProcessTemplateResponse> response = templateController.getById(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().id());
        assertEquals(1, response.getBody().operations().size());
        assertEquals("Распиливание бруса", response.getBody().operations().get(0).name());
    }

    @Test
    @DisplayName("4. PUT /technology-process-templates/{templateId} — обновление названия и описания шаблона")
    void testUpdateTemplate() {
        TechnologyProcessTemplate template = TechnologyProcessTemplate.builder()
                .id(10L)
                .name("Старое название")
                .description("Старое описание")
                .build();

        CreateTechnologyProcessTemplateDto updateDto = new CreateTechnologyProcessTemplateDto(
                "Новое название шаблона",
                "Новое описание шаблона"
        );

        when(templateRepository.findById(10L)).thenReturn(Optional.of(template));
        when(templateRepository.save(any(TechnologyProcessTemplate.class))).thenAnswer(inv -> inv.getArgument(0));
        when(templateOperationRepository.findAllByTemplate_IdOrderByOperationNumberAsc(10L)).thenReturn(List.of());

        ResponseEntity<TechnologyProcessTemplateResponse> response =
                templateController.update(10L, updateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Новое название шаблона", response.getBody().name());
        assertEquals("Новое описание шаблона", response.getBody().description());
    }

    @Test
    @DisplayName("5. DELETE /technology-process-templates/{templateId} — удаление шаблона и проверка 404")
    void testDeleteTemplate() {
        when(templateRepository.existsById(10L)).thenReturn(true);
        when(templateRepository.existsById(999L)).thenReturn(false);

        ResponseEntity<Void> response = templateController.delete(10L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(templateRepository).deleteById(10L);

        assertThrows(EntityNotFoundException.class, () -> templateController.delete(999L));
    }

    @Test
    @DisplayName("6. POST /products/{productId}/technology-process-templates/{templateId}/apply — применение шаблона к новому изделию")
    void testApplyTemplateToNewProduct() {
        TechnologyProcessTemplate template = TechnologyProcessTemplate.builder()
                .id(10L)
                .name("Изготовление деревянной рамы")
                .description("Описание")
                .build();

        TechnologyProcessTemplateOperation tOp1 = TechnologyProcessTemplateOperation.builder()
                .id(201L)
                .template(template)
                .operationNumber(1)
                .name("Распиливание бруса")
                .kind(OperationKind.TECHNOLOGICAL)
                .setupTime(10L)
                .pieceTime(20L)
                .workCenter(prodOp1.getWorkCenter())
                .profession(prodOp1.getProfession())
                .qualification(prodOp1.getQualification())
                .build();

        TechnologyProcessTemplateOperation tOp2 = TechnologyProcessTemplateOperation.builder()
                .id(202L)
                .template(template)
                .operationNumber(2)
                .name("Обработка досок")
                .kind(OperationKind.TECHNOLOGICAL)
                .setupTime(5L)
                .pieceTime(15L)
                .workCenter(prodOp2.getWorkCenter())
                .profession(prodOp2.getProfession())
                .qualification(prodOp2.getQualification())
                .build();

        TechnologyProcessTemplateInput tInput = TechnologyProcessTemplateInput.builder()
                .id(601L)
                .outputTemplateOperation(tOp2)
                .inputTemplateOperation(tOp1)
                .quantity(BigDecimal.valueOf(4))
                .baseQuantity(BigDecimal.ONE)
                .measure(measureUnit)
                .build();

        when(productRepository.findById(2L)).thenReturn(Optional.of(productFrameB));
        when(templateRepository.findById(10L)).thenReturn(Optional.of(template));
        when(templateOperationRepository.findAllByTemplate_IdOrderByOperationNumberAsc(10L))
                .thenReturn(List.of(tOp1, tOp2));
        when(templateInputRepository.findAllByOutputTemplateOperation_IdInOrderByIdAsc(List.of(201L, 202L)))
                .thenReturn(List.of(tInput));

        AtomicLong newProdOpIdSeq = new AtomicLong(400L);
        when(productOperationRepository.save(any(ProductOperation.class))).thenAnswer(inv -> {
            ProductOperation op = inv.getArgument(0);
            op.setId(newProdOpIdSeq.incrementAndGet());
            return op;
        });

        ResponseEntity<List<ProductOperationReponse>> response =
                applicationController.applyTemplate(2L, 10L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        List<ProductOperationReponse> createdOps = response.getBody();
        assertNotNull(createdOps);
        assertEquals(2, createdOps.size());

        // Проверяем, что созданные операции привязаны именно к новому изделию «Деревянная рама модели Б» (id=2)
        assertEquals(401L, createdOps.get(0).id());
        assertEquals("Деревянная рама модели Б", createdOps.get(0).product().getName());
        assertEquals("Распиливание бруса", createdOps.get(0).name());

        assertEquals(402L, createdOps.get(1).id());
        assertEquals("Деревянная рама модели Б", createdOps.get(1).product().getName());
        assertEquals("Обработка досок", createdOps.get(1).name());

        // Проверяем, что связь OperationInput создана между новыми операциями (401 -> 402)
        ArgumentCaptor<OperationInput> inputCaptor = ArgumentCaptor.forClass(OperationInput.class);
        verify(operationInputRepository, times(1)).save(inputCaptor.capture());

        OperationInput savedInput = inputCaptor.getValue();
        assertEquals(402L, savedInput.getOutputOperation().getId());
        assertEquals(401L, savedInput.getInputOperation().getId());
        assertEquals(BigDecimal.valueOf(4), savedInput.getQuantity());
    }
}