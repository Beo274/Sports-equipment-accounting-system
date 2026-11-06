package ru.etu.sport.operations.adapters;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ru.etu.sport.operations.application.TechnologyRouteService;
import ru.etu.sport.operations.dto.TechnologyRouteResponse;

@RestController
@Tag(name = "technology route", description = "technology route management")
@RequestMapping({"/api/technology-routes", "/technology-routes"})
@RequiredArgsConstructor
public class TechnologyRouteController {

    private final TechnologyRouteService technologyRouteService;

    @GetMapping({"/{productId}", "/product/{productId}"})
    public ResponseEntity<TechnologyRouteResponse> getFullRoute(
            @PathVariable Long productId) {
        return ResponseEntity.ok(technologyRouteService.getFullRoute(productId));
    }
}