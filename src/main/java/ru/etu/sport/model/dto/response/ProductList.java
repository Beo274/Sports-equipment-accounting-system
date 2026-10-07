package ru.etu.sport.model.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ProductList {
    private int total;
    private int limit;
    private int offset;
    private List<ProductResponse> items;
}
