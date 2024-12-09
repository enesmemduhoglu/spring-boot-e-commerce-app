package com.ecommerce.shoppingapp.product.repository.mongo;

import com.ecommerce.shoppingapp.product.domain.ProductPrice;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductPriceRepository extends ReactiveMongoRepository<ProductPrice, String> {
}
