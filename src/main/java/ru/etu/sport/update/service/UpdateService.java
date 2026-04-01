package ru.etu.sport.update.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.etu.sport.model.repository.ClassRepository;
import ru.etu.sport.model.repository.ProductRepository;

@Service
@Slf4j
public class UpdateService {
    private final ClassRepository classRepository;
    private final ProductRepository productRepository;

    UpdateService(ClassRepository classRepository, ProductRepository productRepository) {
        this.classRepository = classRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void updateClass(Integer id, Integer parentId) {
        classRepository.updateParentId(id, parentId);
    }

    @Transactional
    public void updateProduct(Integer id, Integer parentId) {
        productRepository.updateParentId(id, parentId);
    }
}
