package ru.etu.sport.attribute.creating;

import org.springframework.stereotype.Service;
import ru.etu.sport.model.entity.Attribute;
import ru.etu.sport.model.repository.AttributeRepository;

@Service
public class CreatingService {
    private AttributeRepository attributeRepository;

    public CreatingService(AttributeRepository attributeRepository) {
        this.attributeRepository = attributeRepository;
    }

    public Attribute create(Attribute attribute) {
        return this.attributeRepository.save(attribute);
    }
}
