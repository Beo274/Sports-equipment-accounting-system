package ru.etu.sport.model.dto.response;

import lombok.Builder;
import lombok.Data;
import ru.etu.sport.model.entity.Product;
import ru.etu.sport.product.model.ProductType;

@Data
@Builder
public class ProductResponse {
    private Integer id;
    private String name;
    private String shortName;
    private ClassResponse baseClass;
    private Integer baseProductId;
    private ProductType type;

    public static ProductResponse from(Product e) {
        return new ProductResponse(
            e.getId(), 
            e.getName(), 
            e.getShortName(), 
            e.getClass() != null ? ClassResponse.from(e.getProductClass()) : null,
            e.getBaseProduct() != null ? e.getBaseProduct().getId() : null,
            e.getType()
        );
    }
}
