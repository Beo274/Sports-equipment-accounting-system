package ru.etu.sport.product.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.etu.sport.model.dto.request.CreateProductDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateConfigurableDto extends CreateProductDto {
    // Простое изделие, для которого мы формируем типовое с конфигуратором,
    // должно быть ProductType.SIMPLE
    @NotNull
    @Positive
    private Long baseProductId;
}
