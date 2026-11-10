package ru.etu.sport.techroute.calc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.etu.sport.techroute.calc.model.dto.OperationCalcStandartsResponseDto;
import ru.etu.sport.techroute.calc.service.CalculationService;

@RestController
@RequestMapping
@Slf4j
@Tag(name = "Расчет сводных норм")
public class CalculationController {
    public final CalculationService calculationService;

    public CalculationController(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    @Operation(summary = "Расчёт норм расхода материалов для операции")
    @GetMapping("/operations/{operationId}/materials")
    public ResponseEntity<OperationCalcStandartsResponseDto> getOperations(@PathVariable Integer operationId, @RequestParam Integer quantity) {
        return ResponseEntity.ok().body(calculationService.getOperationRates(operationId, quantity));
    }

    @Operation(summary = "Расчёт сводных норм расхода материалов для изделия")
    @GetMapping("/products/{productId}/materials/summary")
    public ResponseEntity<OperationCalcStandartsResponseDto> getTotalMaterials(
            @PathVariable Integer productId,
            @RequestParam Integer quantity
    ) {
        return ResponseEntity.ok(
                calculationService.getTotalMaterialRates(
                        productId,
                        quantity
                )
        );
    }

}
