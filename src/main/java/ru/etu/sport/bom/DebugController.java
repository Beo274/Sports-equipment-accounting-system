package ru.etu.sport.bom;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ru.etu.sport.bom.dto.CreateBomRequest;

@RestController 
@RequestMapping ("/debug")
public class DebugController {
    @PostMapping ("/bom")
    public String debug(@Valid @RequestBody CreateBomRequest r) {
        return "parent=" + r.getParentProductId()
             + ", component=" + r.getComponentId()
             + ", measure=" + r.getMeasureUnitId()
             + ", qty=" + r.getQuantity()
             + ", forQty=" + r.getForQuantity()
             + ", isBase=" + r.getIsBase();
    }
}
