package ru.etu.sport.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.etu.sport.attribute.AttributeService;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ValueDto {
    Integer intValue;
    String stringValue;
    String imageValue;

    public void updateValue(AttributeService attributeService, Integer id) {
        if (intValue != null) {
            attributeService.updateValue(this.intValue, id);
        } else if (stringValue != null) {
            attributeService.updateValue(this.stringValue, id);
        }
    }
}
