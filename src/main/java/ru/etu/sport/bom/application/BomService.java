package ru.etu.sport.bom.application;

import java.util.List;

import ru.etu.sport.bom.dto.BomResponse;
import ru.etu.sport.bom.dto.CreateBomRequest;
import ru.etu.sport.bom.dto.UpdateBomRequest;

public interface BomService {
    BomResponse create(CreateBomRequest request);

    BomResponse update(Integer id, UpdateBomRequest request);
    
    void remove(Integer id);

    List<BomResponse> list();

    List<BomResponse> list(List<Integer> ids);

    BomResponse getById(Integer id);
}
