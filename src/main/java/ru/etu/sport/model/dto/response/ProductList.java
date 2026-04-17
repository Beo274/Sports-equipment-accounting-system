package ru.etu.sport.model.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.etu.sport.model.entity.Product;

@Data
@AllArgsConstructor
@Builder
public class ProductList {
    private int total;
    private int limit;
    private int offset;
    private List<Product> items;
}
