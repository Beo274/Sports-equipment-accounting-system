package ru.etu.sport.model.dto.response;

import lombok.Builder;
import lombok.Data;
import ru.etu.sport.model.entity.Product;

@Data
@Builder
public class ProductResponse {
    private Integer id;
    private String name;
    private String shortName;
    private Integer classId;

    public static ProductResponse from(Product e) {
        return new ProductResponse(
            e.getId(), 
            e.getName(), 
            e.getShortName(), 
            e.getClass() != null ? e.getProductClass().getId() : null
        );
    }
}
