package ru.etu.sport.update;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.etu.sport.model.entity.ClassEntity;
import ru.etu.sport.model.entity.Product;
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
    public void updateClass(Integer id, Integer newParentId) {
        ClassEntity currentClass = classRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Class not found"));

        ClassEntity newParent = classRepository.findById(newParentId)
                .orElseThrow(() -> new EntityNotFoundException("New parent not found"));

        currentClass.setBaseClassId(newParent);
    }

    @Transactional
    public void updateProduct(Integer id, Integer newParentId) {
        Product currentProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        ClassEntity newParent = classRepository.findById(newParentId)
                .orElseThrow(() -> new EntityNotFoundException("New parent not found"));
        currentProduct.setClassId(newParent);
    }
}
