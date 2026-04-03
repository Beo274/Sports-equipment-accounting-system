package ru.etu.sport.print.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.etu.sport.model.dto.response.ProductList;
import ru.etu.sport.print.service.ProductsListService;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@Slf4j
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductListController {
    private final ProductsListService productListService;

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
        ProductList response = this.productListService.getProducts(classId, limit, offset);
        log.info("products list provided");
        return ResponseEntity.ok(response);
    }
    
}
