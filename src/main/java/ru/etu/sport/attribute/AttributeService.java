package ru.etu.sport.attribute;

import jakarta.persistence.EntityNotFoundException;
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
        Attribute currentAttribute = attributeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attribute not found with id: " + id));
        currentAttribute.setStringValue(null);
        currentAttribute.setIntValue(null);
    }

    @Transactional
    public void updateValue(Integer val, Integer id) {
        Attribute currentAttribute = attributeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attribute not found with id: " + id));

        currentAttribute.setIntValue(val);
    }

    @Transactional
    public void updateValue(String val, Integer id) {
        Attribute currentAttribute = attributeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attribute not found with id: " + id));

        currentAttribute.setStringValue(val);
    }

//    // image
//    public void updateValue(String val) {
//
//    }
}
