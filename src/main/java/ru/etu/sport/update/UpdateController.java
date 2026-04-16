package ru.etu.sport.update;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class UpdateController {
    private final UpdateService updateService;

    UpdateController(UpdateService updateService) {
        this.updateService = updateService;
    }

    @PutMapping("/class/{id}/swap")
    public ResponseEntity<?> updateClass(@PathVariable Integer id,
                                        @RequestParam("new") Integer parentId) {
        updateService.updateClass(id, parentId);
        return ResponseEntity.status(HttpStatus.OK).body("update successful");
    }

    @PutMapping("/product/{id}/swap")
    public ResponseEntity<?> updateProduct(@PathVariable Integer id,
                                         @RequestParam("new") Integer parentId) {
        updateService.updateProduct(id, parentId);
        log.info("class id for product found successful");
        return ResponseEntity.status(HttpStatus.OK).body("update successful");
    }
}
