package ru.etu.sport.attribute;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.etu.sport.model.entity.Attribute;
import ru.etu.sport.model.repository.AttributeRepository;

@Service
@Slf4j
public class AttributeService {
    private AttributeRepository attributeRepository;

    public AttributeService(AttributeRepository attributeRepository) {
        this.attributeRepository = attributeRepository;
    }

    public Attribute create(Attribute attribute) {
        return this.attributeRepository.save(attribute);
    }

    @Transactional
    public void deleteValue(Integer id) {
        if (this.attributeRepository.getAttributeById(id).getIntValue() != null) {
            this.attributeRepository.deleteIntValueById(id);
            return;
        }
        if (this.attributeRepository.getAttributeById(id).getStringValue() != null) {
            this.attributeRepository.deleteStringValueById(id);
            return;
        }
        if (this.attributeRepository.getAttributeById(id).getImageValue() != null) {
            this.attributeRepository.deleteImageValueById(id);
            return;
        }
    }

    @Transactional
    public void updateValue(Integer val, Integer id) {
        log.info("Update attribute id {}, value {}", id, val);
        this.attributeRepository.updateValue(val, id);
    }

    @Transactional
    public void updateValue(String val, Integer id) {
        log.info("Update attribute id {}, value {}", id, val);
        this.attributeRepository.updateValue(val, id);
    }

//    // image
//    public void updateValue(String val) {
//
//    }
}
