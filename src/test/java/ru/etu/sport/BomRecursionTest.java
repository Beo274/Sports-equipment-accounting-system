package ru.etu.sport;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.etu.sport.bom.dto.BomItemDto;
import ru.etu.sport.bom.infrastructure.BomRepository;
import ru.etu.sport.bom.infrastructure.BomServiceImpl;
import ru.etu.sport.bom.persistence.BomEntity;
import ru.etu.sport.model.entity.ClassEntity;
import ru.etu.sport.model.entity.Product;
import ru.etu.sport.product.ProductRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BomRecursionTest {

    @Mock
    private BomRepository bomRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private BomServiceImpl bomService;

    private Product createProduct(Integer id, String name, Integer classId) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setShortName(name);

        ClassEntity c = new ClassEntity();
        c.setId(classId);
        p.setProductClass(c);
        return p;
    }

    private BomEntity createBom(Product parent, Product component, int quantity, int forQuantity) {
        return BomEntity.builder()
                .parentProduct(parent)
                .component(component)
                .quantity(quantity)
                .forQuantity(forQuantity)
                .build();
    }

    @Test
    @DisplayName("Проверка расчета: A x 5 -> (B x 2 -> C x 3 -> D x 4) = D: 120")
    void testDeepMultiplication() {
        Product prodA = createProduct(1, "A", 1);
        Product prodB = createProduct(2, "B", 1);
        Product prodC = createProduct(3, "C", 1);
        Product prodD = createProduct(4, "D", 2);

        when(productRepository.findById(1L)).thenReturn(Optional.of(prodA));
        when(bomRepository.findAllByParentProduct(prodA)).thenReturn(List.of(createBom(prodA, prodB, 2, 1)));
        when(bomRepository.findAllByParentProduct(prodB)).thenReturn(List.of(createBom(prodB, prodC, 3, 1)));
        when(bomRepository.findAllByParentProduct(prodC)).thenReturn(List.of(createBom(prodC, prodD, 4, 1)));
        when(bomRepository.findAllByParentProduct(prodD)).thenReturn(Collections.emptyList());

        List<BomItemDto> result = bomService.calcBom(1, 5.0);

        assertEquals(1, result.size());
        assertEquals("D", result.get(0).component().getName());
        assertEquals(120.0, result.get(0).quantity());
    }

    @Test
    @DisplayName("Проверка фильтрации по classId")
    void testFilterByClassId() {
        Product prodA = createProduct(1, "A", 1);
        Product prodB = createProduct(2, "B", 1);
        Product prodD = createProduct(4, "D", 999);

        when(productRepository.findById(1L)).thenReturn(Optional.of(prodA));
        when(bomRepository.findAllByParentProduct(prodA)).thenReturn(List.of(
                createBom(prodA, prodB, 2, 1),
                createBom(prodA, prodD, 10, 1)
        ));
        when(bomRepository.findAllByParentProduct(prodB)).thenReturn(Collections.emptyList());
        when(bomRepository.findAllByParentProduct(prodD)).thenReturn(Collections.emptyList());

        // Получаем список в переменную filtered:
        List<BomItemDto> filtered = bomService.calcBom(1, 1.0, 999);

        // Проверяем именно переменную filtered (не result!):
        assertEquals(1, filtered.size());
        assertEquals("D", filtered.get(0).component().getName());
        assertEquals(10.0, filtered.get(0).quantity());
    }

    @Test
    @DisplayName("Проверка защиты от цикла: A -> B -> C -> A не должна уходить в бесконечную рекурсию")
    void testCycleDetection() {
        Product prodA = createProduct(1, "A", 1);
        Product prodB = createProduct(2, "B", 1);
        Product prodC = createProduct(3, "C", 1);

        when(productRepository.findById(1L)).thenReturn(Optional.of(prodA));
        when(bomRepository.findAllByParentProduct(prodA)).thenReturn(List.of(createBom(prodA, prodB, 1, 1)));
        when(bomRepository.findAllByParentProduct(prodB)).thenReturn(List.of(createBom(prodB, prodC, 1, 1)));
        when(bomRepository.findAllByParentProduct(prodC)).thenReturn(List.of(createBom(prodC, prodA, 1, 1)));

        assertThrows(IllegalStateException.class, () -> {
            bomService.calcBom(1, 1.0);
        });
    }
}