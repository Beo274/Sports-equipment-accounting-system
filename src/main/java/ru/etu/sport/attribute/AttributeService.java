package ru.etu.sport.attribute;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.etu.sport.model.entity.Attribute;
import ru.etu.sport.model.entity.Measure;
import ru.etu.sport.repository.AttributeRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AttributeService {
    private final AttributeRepository attributeRepository;

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

    @Transactional
    public void updateValue(String val, Integer id, boolean isImage) {
        Attribute currentAttribute = attributeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attribute not found with id: " + id));
        currentAttribute.setImageValue(val);
    }

    public List<Map<String, Object>> getAttributesByClassId(Integer classId) {
        List<Attribute> attributes = attributeRepository.findByClassId_Id(classId);
        return attributes.stream().map(attr -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", attr.getName());
            Object val = attr.getStringValue() != null ? attr.getStringValue() :
                    (attr.getIntValue() != null ? attr.getIntValue() : attr.getImageValue());
            map.put("val", val);
            return map;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void setMeasure(Integer id, Measure measure) {
        Attribute currentAttribute = attributeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attribute not found with id: " + id));
        currentAttribute.setMeasure(measure);
    }
}