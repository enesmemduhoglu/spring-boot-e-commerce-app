package com.ecommerce.shoppingapp.product.service;

import org.springframework.stereotype.Service;
import com.ecommerce.shoppingapp.product.domain.MoneyTypes;
import java.math.BigDecimal;

@Service
public class ProductDeliveryService {

    public String getDeliveryInfo(String productId) {
        // TODO
        return "Tomorrow";
    }

    public boolean freeDeliveryCheck(String productId, BigDecimal price, MoneyTypes moneyType) {
        // TODO
        return true;
    }
}
