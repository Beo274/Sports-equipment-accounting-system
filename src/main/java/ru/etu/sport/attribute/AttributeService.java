package ru.etu.sport.attribute;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import ru.etu.sport.model.dto.request.CreateAttributeDto;
import ru.etu.sport.model.dto.request.CreateAttributeValueDto;
import ru.etu.sport.model.entity.Attribute;
import ru.etu.sport.model.entity.AttributeValue;
import ru.etu.sport.model.entity.Measure;
import ru.etu.sport.repository.AttributeRepository;
import ru.etu.sport.repository.AttributeValueRepository;
import ru.etu.sport.repository.MeasureRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttributeService {
    private final AttributeRepository attributeRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final MeasureRepository measureRepository;

    public Attribute create(CreateAttributeDto createAttributeDto) {
        return this.attributeRepository.save(createAttributeDto.toAttribute());
    }

    @Transactional
    public AttributeValue createValue(CreateAttributeValueDto createAttributeValueDto) {
        AttributeValue value = new AttributeValue();
        value.setName(createAttributeValueDto.getName());
        value.setShortName(createAttributeValueDto.getShortName());

        Attribute attributeProxy = attributeRepository.getReferenceById(createAttributeValueDto.getAttributeId());
        value.setAttribute(attributeProxy);

        if (createAttributeValueDto.getIntValue() != null) {
            value.setIntValue(createAttributeValueDto.getIntValue());
        } else if (createAttributeValueDto.getStringValue() != null) {
            value.setStringValue(createAttributeValueDto.getStringValue());
        } else if (createAttributeValueDto.getImageValue() != null) {
            value.setImageValue(createAttributeValueDto.getImageValue());
        }

        if (createAttributeValueDto.getMeasureId() != null) {
            Measure measureProxy = measureRepository.getReferenceById(createAttributeValueDto.getMeasureId());
            value.setMeasure(measureProxy);
        }
        
        return this.attributeValueRepository.save(value);
    }

    public List<AttributeValue> listAttributeValues(Integer attributeId) {
        return this.attributeValueRepository.findByAtributeId(attributeId);
    }

    public void delete(Integer id) {
        this.attributeRepository.deleteById(id);
    }

    @Transactional
    public void deleteValue(Integer id, DeleteValueOption option) {
        AttributeValue value = this.attributeValueRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("AttributeValue not found with id: " + id));
        switch (option) {
            case FULL:
                this.attributeValueRepository.deleteById(id);
                break;
            case INT:
                value.setIntValue(null);
                break;
            case STRING:
                value.setStringValue(null);
                break;
            case IMAGE:
                value.setImageValue(null);
                break;
        }
    }

    public void deleteAttribute(Integer id) {
        this.attributeRepository.deleteById(id);
    }

    @Transactional
    public void updateValue(Integer val, Integer id) {
        AttributeValue value = this.attributeValueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attribute not found with id: " + id));
        value.setIntValue(val);
    }

    @Transactional
    public void updateValue(String val, Integer id) {
        AttributeValue value = this.attributeValueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attribute not found with id: " + id));
        value.setStringValue(val);
    }

    @Transactional
    public void updateValue(String val, Integer id, boolean isImage) {
        AttributeValue value = this.attributeValueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attribute not found with id: " + id));
        value.setImageValue(val);
    }

    @Transactional
    public void setMeasure(Integer id, Measure measure) {
        AttributeValue value = this.attributeValueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attribute not found with id: " + id));
        value.setMeasure(measure);
    }

    public List<Attribute> listAttributes() {
        return this.attributeRepository.findAll();
    }

    public AttributeValue getAttributeValue(Integer id) {
        AttributeValue value = this.attributeValueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Attribute value with id: %d not found", id)));
        return value;
    }

    @Transactional
    public void reorderValues(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        List<AttributeValue> values = this.attributeValueRepository.findAllById(ids);

        Map<Integer, Integer> idToPosition = new HashMap<>();
        for (Integer i = 0; i < ids.size(); i++) {
            idToPosition.put(ids.get(i), i + 1);
        }

        for (AttributeValue value : values) {
            value.setPosition(idToPosition.get(value.getId()));
        }
    }
}