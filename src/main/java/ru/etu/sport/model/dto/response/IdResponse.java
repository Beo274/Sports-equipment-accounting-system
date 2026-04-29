package ru.etu.sport.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IdResponse {

    @Schema(example = "1")
    private Integer id;
}
