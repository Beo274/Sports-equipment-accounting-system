package ru.etu.sport.product;

import java.util.Map;

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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.etu.sport.model.dto.request.CreateProductDto;
import ru.etu.sport.model.dto.response.IdResponse;
import ru.etu.sport.model.dto.response.ProductList;


@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<IdResponse> addProduct(@Valid @RequestBody CreateProductDto createProductDto) {
        Integer id = productService.addProduct(createProductDto);
        log.info("Product created");
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable("id") Integer id) {
        productService.deleteProduct(id);
        log.info("Product deleted");
        return ResponseEntity.ok(Map.of("message", "deleted"));
    }

    @GetMapping
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
    public ResponseEntity<?> updateProduct(@PathVariable("id") Integer id, @RequestParam("new") Integer parentId) {
        productService.updateClassId(id, parentId);
        log.info("class id for product found successful");
        return ResponseEntity.status(HttpStatus.OK).body("update successful");
    }
}
