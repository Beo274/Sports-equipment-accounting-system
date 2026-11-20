package ru.etu.sport.product;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.etu.sport.model.dto.response.ProductResponse;
import ru.etu.sport.product.application.ProductConfigurationService;
import ru.etu.sport.product.dto.ConfigurationParameterDto;
import ru.etu.sport.product.dto.ConfigurationResponse;
import ru.etu.sport.product.dto.CreateConfigurableDto;
import ru.etu.sport.product.dto.CreateVariantDto;
import ru.etu.sport.product.dto.VariantResponse;

@RestController
@RequestMapping("/product")
@Tag(name = "configuration and variant", description = "create product configuration and variants")
@RequiredArgsConstructor
public class ProductConfigurationController {
    private final ProductConfigurationService productConfigurationService;

    @PostMapping("/configuration")
    @Operation( summary = "creates product template")
    public ResponseEntity<ProductResponse> createConfigurable(@Valid @RequestBody CreateConfigurableDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productConfigurationService.createConfigurable(dto));
    }

    @PatchMapping("/configuration/{id}")
    @Operation( summary = "overrides or extends configuration parameters list")
    public ResponseEntity<ConfigurationResponse> extendConfiguration(@PathVariable Integer id, @Valid @RequestBody ConfigurationParameterDto dto) {
        return ResponseEntity.ok(productConfigurationService.extendConfiguration(id, dto));
    }

    @PostMapping("/variant")
    @Operation( summary = "creates variant product by configurable")
    public ResponseEntity<VariantResponse> createVariant(@Valid @RequestBody CreateVariantDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productConfigurationService.createVariant(dto));
    }

    @GetMapping("/configuration")
    public ResponseEntity<List<ConfigurationResponse>> getAll() {
        return ResponseEntity.ok(productConfigurationService.getAll());
    }

    @GetMapping("/configuration/{id}")
    public ResponseEntity<ConfigurationResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(productConfigurationService.getById(id));
    }

    @DeleteMapping("/configuration/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        productConfigurationService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/configuration/{id}/{parameterId}")
    @Operation( summary = "deletes parameter with parameterId from configurable with id")
    public ResponseEntity<Void> deleteParameter(@PathVariable Integer id, @PathVariable Integer parameterId) {
        productConfigurationService.deleteConfigurationParameter(id, parameterId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
