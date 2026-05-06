package ru.etu.sport.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.etu.sport.enumeration.EnumerationService;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValueDto {
    Integer intValue;
    String stringValue;
    String imageValue;

    public void updateValue(EnumerationService enumerationService, Integer id) {
        if (intValue != null) {
            enumerationService.updateValue(this.intValue, id);
        } else if (stringValue != null) {
            enumerationService.updateValue(this.stringValue, id);
        } else if (imageValue != null) {
            enumerationService.updateValue(this.imageValue, id, true);
        }
    }
}
