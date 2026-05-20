package ru.etu.sport.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
    private Integer id;
    private String name;
    private String shortName;
    private Integer classId;
}
