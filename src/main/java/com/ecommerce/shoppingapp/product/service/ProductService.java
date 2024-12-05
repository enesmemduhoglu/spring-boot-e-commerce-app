package com.ecommerce.shoppingapp.product.service;

import com.ecommerce.shoppingapp.product.model.ProductResponse;
import com.ecommerce.shoppingapp.product.model.ProductSaveRequest;
import com.ecommerce.shoppingapp.product.repository.mongo.ProductRepository;
import com.ecommerce.shoppingapp.repository.es.ProductEsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductEsRepository productEsRepository;
    private ProductRepository productRepository;

    List<ProductResponse> getByPaging(Pageable pageable){
        return null;
    }

    ProductResponse save(ProductSaveRequest productSaveRequest){
        return null;
    }

}
