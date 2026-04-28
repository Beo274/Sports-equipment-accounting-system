package ru.etu.sport.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.etu.sport.model.entity.Attribute;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAttributeDto {
    @NotNull
    private String name;

    @NotNull
    private String shortName;

    public Attribute toAttribute() {
        Attribute attribute = new Attribute();
        attribute.setName(this.name);
        attribute.setShortName(this.shortName);
        return attribute;
    }
}
