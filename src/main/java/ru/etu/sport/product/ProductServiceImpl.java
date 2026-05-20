package ru.etu.sport.product;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.etu.sport.model.dto.request.CreateProductDto;
import ru.etu.sport.model.dto.response.ProductList;
import ru.etu.sport.model.dto.response.ProductResponse;
import ru.etu.sport.model.entity.ClassEntity;
import ru.etu.sport.model.entity.Product;
import ru.etu.sport.category.ClassRepository;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ClassRepository classRepository;

    @Override
    public void deleteProduct(Integer id) {
        productRepository.delete(id);
    }

    @Override
    @Transactional
    public ProductList getProducts(Integer classId, Integer limit, Integer offset) {
        List<Product> products;
        Integer count;

        Pageable pageable = PageRequest.of(offset / limit, limit);

        if (classId == null) {
            products = this.productRepository.getProducts(pageable);
            count = this.productRepository.countProducts();
        } else {
            products = this.productRepository.findByClassId(classId, pageable);
            count = this.productRepository.countProducts(classId);
        }

        List<ProductResponse> mappedProducts = products.stream()
            .map(p -> ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .shortName(p.getShortName())
                .classId(p.getProductClass().getId())
                .build())
            .toList();

        return new ProductList(count, limit, offset, mappedProducts);
    }

    @Override
    public void updateClassId(Integer id, Integer parentId) {
        productRepository.updateParentId(id, parentId);
    }

    @Override
    public Integer addProduct(CreateProductDto createProductDto) {
        Product product = new Product();

        product.setName(createProductDto.getName());
        product.setShortName(createProductDto.getShortName());
        ClassEntity baseClass = this.classRepository.getReferenceById(createProductDto.getClassId());
        product.setProductClass(baseClass);

        return productRepository.save(product).getId();
    }
}
