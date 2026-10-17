package ru.etu.sport.bom.application;

import java.util.List;

import ru.etu.sport.bom.dto.BomItemDto;
import ru.etu.sport.bom.dto.BomResponse;
import ru.etu.sport.bom.dto.BomTreeDto;
import ru.etu.sport.bom.dto.CreateBomRequest;
import ru.etu.sport.bom.dto.UpdateBomRequest;

public interface BomService {
    BomResponse create(CreateBomRequest request);

    BomResponse update(Integer id, UpdateBomRequest request);

    void remove(Integer id);

    List<BomResponse> list();

    List<BomResponse> list(List<Integer> ids);

    BomResponse getById(Integer id);

    void copyBom(Long baseProductId, Long newProductId);

    BomTreeDto findBomTree(Integer productId);

    List<BomItemDto> findBomLeaves(Integer productId);

    List<BomItemDto> calcBom(Integer productId, double quantity);

    List<BomItemDto> calcBom(Integer productId, double quantity, Integer classId);
}