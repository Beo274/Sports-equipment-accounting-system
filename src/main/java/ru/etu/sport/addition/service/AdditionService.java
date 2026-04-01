package ru.etu.sport.addition.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.etu.sport.model.entity.ClassEntity;
import ru.etu.sport.model.repository.ClassRepository;

@Service
public class AdditionService {

    private final ClassRepository classRepository;

    public AdditionService(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    public Integer addClass(ClassEntity classEntity) {
        return classRepository.save(classEntity).getId();
    }
}
