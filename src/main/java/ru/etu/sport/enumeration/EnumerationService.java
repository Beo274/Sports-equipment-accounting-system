package ru.etu.sport.enumeration;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import ru.etu.sport.model.dto.request.CreateEnumerationValueDto;
import ru.etu.sport.model.dto.request.CreateEnumerationDto;
import ru.etu.sport.model.entity.Enumeration;
import ru.etu.sport.model.entity.EnumerationValue;
import ru.etu.sport.model.entity.Measure;
import ru.etu.sport.repository.EnumerationRepository;
import ru.etu.sport.repository.EnumerationValueRepository;
import ru.etu.sport.repository.MeasureRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnumerationService {
    private final EnumerationRepository enumerationRepository;
    private final EnumerationValueRepository enumerationValueRepository;
    private final MeasureRepository measureRepository;

    public Enumeration create(CreateEnumerationDto createEnumerationDto) {
        return this.enumerationRepository.save(createEnumerationDto.toEnumeration());
    }

    @Transactional
    public EnumerationValue createValue(CreateEnumerationValueDto createEnumerationValueDto) {
        EnumerationValue value = new EnumerationValue();

        Enumeration enumerationProxy = enumerationRepository.getReferenceById(createEnumerationValueDto.getEnumerationId());
        value.setEnumeration(enumerationProxy);

        if (createEnumerationValueDto.getIntValue() != null) {
            value.setIntValue(createEnumerationValueDto.getIntValue());
        } else if (createEnumerationValueDto.getStringValue() != null) {
            value.setStringValue(createEnumerationValueDto.getStringValue());
        } else if (createEnumerationValueDto.getImageValue() != null) {
            value.setImageValue(createEnumerationValueDto.getImageValue());
        }

        if (createEnumerationValueDto.getMeasureId() != null) {
            Measure measureProxy = measureRepository.getReferenceById(createEnumerationValueDto.getMeasureId());
            value.setMeasure(measureProxy);
        }
        
        return this.enumerationValueRepository.save(value);
    }

    public List<EnumerationValue> listEnumerationValues(Integer enumerationId) {
        return this.enumerationValueRepository.findByAtributeId(enumerationId);
    }

    public void delete(Integer id) {
        this.enumerationRepository.deleteById(id);
    }

    @Transactional
    public void deleteValue(Integer id, DeleteValueOption option) {
        EnumerationValue value = this.enumerationValueRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("EnumerationValue not found with id: " + id));
        switch (option) {
            case FULL:
                this.enumerationValueRepository.deleteById(id);
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

    public void deleteEnumeration(Integer id) {
        this.enumerationRepository.deleteById(id);
    }

    @Transactional
    public void updateValue(Integer val, Integer id) {
        EnumerationValue value = this.enumerationValueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enumeration not found with id: " + id));
        value.setIntValue(val);
    }

    @Transactional
    public void updateValue(String val, Integer id) {
        EnumerationValue value = this.enumerationValueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enumeration not found with id: " + id));
        value.setStringValue(val);
    }

    @Transactional
    public void updateValue(String val, Integer id, boolean isImage) {
        EnumerationValue value = this.enumerationValueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enumeration not found with id: " + id));
        value.setImageValue(val);
    }

    @Transactional
    public void setMeasure(Integer id, Measure measure) {
        EnumerationValue value = this.enumerationValueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enumeration not found with id: " + id));
        value.setMeasure(measure);
    }

    public List<Enumeration> listEnumerations() {
        return this.enumerationRepository.findAll();
    }

    public EnumerationValue getEnumerationValue(Integer id) {
        EnumerationValue value = this.enumerationValueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Enumeration value with id: %d not found", id)));
        return value;
    }

    @Transactional
    public void reorderValues(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        List<EnumerationValue> values = this.enumerationValueRepository.findAllById(ids);

        Map<Integer, Integer> idToPosition = new HashMap<>();
        for (Integer i = 0; i < ids.size(); i++) {
            idToPosition.put(ids.get(i), i + 1);
        }

        for (EnumerationValue value : values) {
            value.setPosition(idToPosition.get(value.getId()));
        }
    }
}