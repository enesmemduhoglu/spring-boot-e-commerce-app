package com.ecommerce.shoppingapp.product.service;

import com.ecommerce.shoppingapp.product.domain.Product;
import com.ecommerce.shoppingapp.product.domain.es.CategoryEs;
import com.ecommerce.shoppingapp.product.domain.es.CompanyEs;
import com.ecommerce.shoppingapp.product.domain.es.ProductEs;
import com.ecommerce.shoppingapp.product.repository.es.ProductEsRepository;
import com.ecommerce.shoppingapp.product.repository.mongo.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductEsService {

    private final ProductRepository productRepository;
    private final ProductEsRepository productEsRepository;

    public Mono<ProductEs> saveNewProduct(Product product) {
        return productEsRepository.save(
                ProductEs.builder()
                        .active(product.getActive())
                        .code(product.getCode())
                        .description(product.getDescription())
                        .features(product.getFeatures())
                        .id(product.getId())
                        .name(product.getName())
                        // TODO get company name and code
                        .seller(CompanyEs.builder().id(product.getCompanyId()).name("Test").build())
                        // TODO get company name and code
                        .category(CategoryEs.builder().id(product.getCategoryId()).name("Test").build())
                        .build());
    }

    public Flux<ProductEs> findAll() {
        return productEsRepository.findAll();
    }
}
