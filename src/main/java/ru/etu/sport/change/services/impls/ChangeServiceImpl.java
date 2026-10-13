package ru.etu.sport.change.services.impls;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.etu.sport.change.entities.ProductChange;
import ru.etu.sport.change.entities.dto.ProductChangeRequest;
import ru.etu.sport.change.entities.dto.ProductChangeResponse;
import ru.etu.sport.change.entities.dto.UpdateProductChangeRequest;
import ru.etu.sport.change.entities.repository.ProductChangeRepository;
import ru.etu.sport.change.services.ChangeService;
import ru.etu.sport.model.entity.Product;
import ru.etu.sport.product.ProductRepository;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ChangeServiceImpl implements ChangeService {
    private final ProductChangeRepository productChangeRepository;
    private final ProductRepository productRepository;

    public ChangeServiceImpl(ProductChangeRepository productChangeRepository,
                             ProductRepository productRepository) {
        this.productChangeRepository = productChangeRepository;
        this.productRepository =  productRepository;
    }


    @Override
    public ProductChangeResponse create(ProductChangeRequest request) {
        ProductChange change = new ProductChange();
        change.setDate(request.getDate());
        if (request.getProduct() != null) {
            change.setProduct(productRepository.findById(request.getProduct()).orElseThrow((NoSuchElementException::new)));
        }
        change.setDescription(request.getDescription());

        productChangeRepository.save(change);

        return ProductChangeResponse.from(change);
    }

    @Override
    @Transactional
    public ProductChangeResponse update(Long id, UpdateProductChangeRequest request) {
        ProductChange change = productChangeRepository.findById(id).orElseThrow((NoSuchElementException::new));
        if (change.getDescription() != null) {
            change.setDescription(request.getDescription());
        }

        if (change.getDate() != null) {
            change.setDate(request.getDate());
        }

        if (change.getProduct() != null) {
            Product product = productRepository.findById(request.getProduct()).orElseThrow((NoSuchElementException::new));
            change.setProduct(product);
        }

        return ProductChangeResponse.from(change);

    }

    @Override
    @Transactional
    public Long delete(Long id) {
        productChangeRepository.delete(productChangeRepository.findById(id).orElseThrow((NoSuchElementException::new)));
        return id;
    }

    @Override
    public List<ProductChangeResponse> getAll() {
        return productChangeRepository.findAll(Sort.by("product_id","date"))
                .stream()
                .map(ProductChangeResponse::from)
                .collect(Collectors.toList()
        );
    }

    @Override
    public List<ProductChangeResponse> getForProduct(Long product_id) {
        Product product =  productRepository.findById(product_id).orElseThrow((NoSuchElementException::new));
        return productChangeRepository.findAllByProduct(product)
                .stream()
                .map(ProductChangeResponse::from)
                .sorted(Comparator.comparing(ProductChangeResponse::date))
                .collect(Collectors.toList());
    }
}
