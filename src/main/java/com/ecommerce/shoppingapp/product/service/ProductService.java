package com.ecommerce.shoppingapp.product.service;

import com.ecommerce.shoppingapp.product.domain.MoneyTypes;
import com.ecommerce.shoppingapp.product.domain.Product;
import com.ecommerce.shoppingapp.product.domain.ProductImage;
import com.ecommerce.shoppingapp.product.domain.es.ProductEs;
import com.ecommerce.shoppingapp.product.model.product.ProductDetailResponse;
import com.ecommerce.shoppingapp.product.model.product.ProductResponse;
import com.ecommerce.shoppingapp.product.model.product.ProductSaveRequest;
import com.ecommerce.shoppingapp.product.model.ProductSellerResponse;
import com.ecommerce.shoppingapp.product.repository.mongo.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.ecommerce.shoppingapp.product.domain.ProductImage.ImageType.FEATURE;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDeliveryService productDeliveryService;
    private final ProductAmountService productAmountService;
    private final ProductEsService productEsService;

    public Flux<ProductResponse> getAll() {
        return productEsService.findAll().map(this::mapToDto);
    }

    public ProductResponse save(ProductSaveRequest request) {
        Product product = Product.builder()
                .active(Boolean.TRUE)
                .code("PR0001")
                .categoryId(request.getCategoryId())
                .companyId(request.getSellerId())
                .description(request.getDescription())
                .features(request.getFeatures())
                .name(request.getName())
                .price(request.getPrice())
                .productImage(request.getImages().stream().map(it -> new ProductImage(FEATURE, it)).collect(toList()))
                .build();
        product = productRepository.save(product).block();
        return this.mapToDto(productEsService.saveNewProduct(product).block());
    }

    /**
     * 2 - Calc fieldlari isle
     * 3 - redisten ihtiyac alanlarini getir
     * 4 - response nesnesine donustur
     *
     * @return
     */
    private ProductResponse mapToDto(ProductEs item) {
        if (item == null) {
            return null;
        }

        return ProductResponse.builder()
                // TODO client request uzerinden validate edilecek
                .price(item.getPrice().get("USD"))
                .moneySymbol(MoneyTypes.USD.getSymbol())
                .name(item.getName())
                .features(item.getFeatures())
                .id(item.getId())
                .description(item.getDescription())
                .deliveryIn(productDeliveryService.getDeliveryInfo(item.getId()))
                .categoryId(item.getCategory().getId())
                .available(productAmountService.getByProductId(item.getId()))
                .freeDelivery(productDeliveryService.freeDeliveryCheck(item.getId(), item.getPrice().get("USD"), MoneyTypes.USD))
                .image(item.getImages().get(0))
                .seller(ProductSellerResponse.builder().id(item.getSeller().getId()).name(item.getSeller().getName()).build())
                .build();
    }

    public Mono<Long> count() {
        return productRepository.count();
    }

    public Mono<ProductDetailResponse> getProductDetail(String id) {
        return this.mapToDto(productEsService.findById(id));
    }

    private Mono<ProductDetailResponse> mapToDto(Mono<ProductEs> product) {
        return product.map( item -> ProductDetailResponse.builder()
                .price(item.getPrice().get("USD"))
                .moneySymbol(MoneyTypes.USD.getSymbol())
                .name(item.getName())
                .features(item.getFeatures())
                .id(item.getId())
                .description(item.getDescription())
                .deliveryIn(productDeliveryService.getDeliveryInfo(item.getId()))
                .categoryId(item.getCategory().getId())
                .available(productAmountService.getByProductId(item.getId()))
                .freeDelivery(productDeliveryService.freeDeliveryCheck(item.getId(), item.getPrice().get("USD"), MoneyTypes.USD))
                .images(item.getImages())
                .seller(ProductSellerResponse.builder().id(item.getSeller().getId()).name(item.getSeller().getName()).build())
                .build());
    }
}