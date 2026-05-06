package ru.etu.sport.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.etu.sport.model.entity.Enumeration;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEnumerationDto {
    @NotNull
    private String name;

    @NotNull
    private String shortName;

    public Enumeration toEnumeration() {
        Enumeration enumeration = new Enumeration();
        enumeration.setName(this.name);
        enumeration.setShortName(this.shortName);
        return enumeration;
    }
}
