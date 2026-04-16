package ru.etu.sport.addition;

import org.springframework.stereotype.Service;
import ru.etu.sport.model.entity.ClassEntity;
import ru.etu.sport.model.entity.Product;
import ru.etu.sport.model.repository.ClassRepository;
import ru.etu.sport.model.repository.ProductRepository;

@Service
public class AdditionService {

    private final ClassRepository classRepository;
    private final ProductRepository productRepository;

    public AdditionService(ClassRepository classRepository, ProductRepository productRepository) {
        this.classRepository = classRepository;
        this.productRepository = productRepository;
    }

    public Integer addClass(ClassEntity classEntity) {
        return classRepository.save(classEntity).getId();
    }

    public Integer addProduct(Product product) {
        return productRepository.save(product).getId();
    }
}
