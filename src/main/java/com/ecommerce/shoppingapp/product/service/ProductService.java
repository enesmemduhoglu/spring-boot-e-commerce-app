package com.ecommerce.shoppingapp.product.service;

import com.ecommerce.shoppingapp.product.domain.MoneyTypes;
import com.ecommerce.shoppingapp.product.domain.Product;
import com.ecommerce.shoppingapp.product.domain.ProductImage;
import com.ecommerce.shoppingapp.product.domain.es.ProductEs;
import com.ecommerce.shoppingapp.product.model.ProductResponse;
import com.ecommerce.shoppingapp.product.model.ProductSaveRequest;
import com.ecommerce.shoppingapp.product.model.ProductSellerResponse;
import com.ecommerce.shoppingapp.product.repository.mongo.ProductRepository;
import com.ecommerce.shoppingapp.product.repository.es.ProductEsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import static com.ecommerce.shoppingapp.product.domain.ProductImage.ImageType.FEATURE;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductEsService productEsService;
    private ProductRepository productRepository;
    private final ProductPriceService productPriceService;
    private final ProductDeliveryService productDeliveryService;
    private final ProductAmountService productAmountService;
    private final ProductImageService productImageService;


    public Flux<ProductResponse> getAll() {
        return productEsService.findAll().map(this::mapToDto);
    }

    public ProductResponse save(ProductSaveRequest request){
        Product product = Product.builder()
                .active(Boolean.TRUE)
                .code("PR0001")
                .categoryId(request.getCategoryId())
                .companyId(request.getSellerId())
                .description(request.getDescription())
                .features(request.getFeatures())
                .name(request.getName())
                .productImage(request.getImages().stream().map(it -> new ProductImage(FEATURE , it)).collect(toList()))
                .build();
        product = productRepository.save(product).block();

        productEsService.saveNewProduct(product);

        return this.mapToDto(productEsService.saveNewProduct(product).block());
    }


    private ProductResponse mapToDto(ProductEs item) {
        if (item == null){
            return null;
        }
        BigDecimal productPrice = productPriceService.getByMoneyType(item.getId(), MoneyTypes.USD);
        return ProductResponse.builder()
                .price(productPrice)
                .name(item.getName())
                .features(item.getFeatures())
                .id(item.getId())
                .description(item.getDescription())
                .deliveryIn(productDeliveryService.getDeliveryInfo(item.getId()))
                .categoryId(item.getCategory().getId())
                .available(productAmountService.getByProductId(item.getId()))
                .freeDelivery(productDeliveryService.freeDeliveryCheck(item.getId(), productPrice))
                .moneyType(MoneyTypes.USD)
                .image(productImageService.getProductMainImage(item.getId()))
                .seller(ProductSellerResponse.builder().id(item.getSeller().getId()).name((item.getSeller().getName())).build())
                .build();
    }

    public Mono<Long> count() {
        return productRepository.count();
    }
}
