package ru.etu.sport.model.dto.request;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReorderDto {
    List<Integer> order;
}
