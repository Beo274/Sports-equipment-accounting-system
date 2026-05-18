package ru.etu.sport.product;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.etu.sport.model.dto.request.CreateProductDto;
import ru.etu.sport.model.dto.response.IdResponse;
import ru.etu.sport.model.dto.response.MessageResponse;
import ru.etu.sport.model.dto.response.ProductList;
import ru.etu.sport.model.dto.response.ProductWithParamDto;
import ru.etu.sport.parameter.ClassProductParameterService;


@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "products", description = "Products managing endpoints")
public class ProductController {
    private final ProductService productService;
    private final ClassProductParameterService classProductParameterService;

    @PostMapping
    public ResponseEntity<IdResponse> addProduct(@Valid @RequestBody CreateProductDto createProductDto) {
        Integer id = productService.addProduct(createProductDto);
        log.info("Product created");
        this.classProductParameterService.inheritParametersForProduct(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteProduct(@PathVariable("id") Integer id) {
        productService.deleteProduct(id);
        log.info("Product deleted");
        return ResponseEntity.ok(new MessageResponse("deleted"));
    }

    @GetMapping("/getAll")
    public ResponseEntity<ProductList> listProducts(
        @RequestParam(required = false) Integer limit,
        @RequestParam(required = false) Integer offset,
        @RequestParam(required = false) Integer classId
    ) {
        if (offset == null) {
            offset = 0;
        }

        if (limit == null) {
            limit = 10;
        }
        ProductList response = this.productService.getProducts(classId, limit, offset);
        log.info("products list provided");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/swap")
    public ResponseEntity<MessageResponse> updateProduct(@PathVariable("id") Integer id, @RequestParam("new") Integer parentId) {
        productService.updateClassId(id, parentId);
        log.info("class id for product found successful");
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("updated"));
    }

    @GetMapping("/params")
    public ResponseEntity<Map<String, Object>> getProductsWithParams(@RequestParam("classId") Integer classId) {
        return ResponseEntity.ok(classProductParameterService.getProductsWithParamsByClass(classId));
    }

    @GetMapping
    @Operation(summary = "По диапазону",
               description = "Получение продуктов с параметрами по диапазону числового значения параметра")
    public ResponseEntity<List<ProductWithParamDto>> getProductsInRange(@RequestParam(value = "paramId") Integer paramId,
                                                                        @RequestParam(value = "minVal")  Double minVal,
                                                                        @RequestParam(value = "maxVal")  Double maxVal) {
        return ResponseEntity.ok(classProductParameterService.getProductsInRange(paramId, minVal, maxVal));
    }

    @GetMapping("/search-by-params")
    @Operation(
            summary = "Получение продуктов с фильтрацией по нескольким параметрам",
            description = "Возвращает продукты, которые ОДНОВРЕМЕННО обладают всеми перечисленными ID параметров. Возвращает продукты со ВСЕМ набором их характеристик."
    )
    public ResponseEntity<List<ProductWithParamDto>> getProductsByParams(
            @RequestParam(value = "paramIds", required = false) List<Integer> paramIds) {

        if (paramIds == null || paramIds.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(classProductParameterService.getProductsBySeveralParams(paramIds));
    }
    
}
