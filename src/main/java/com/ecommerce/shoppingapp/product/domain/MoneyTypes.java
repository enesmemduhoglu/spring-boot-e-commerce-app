package com.ecommerce.shoppingapp.product.domain;

public enum MoneyTypes {
    USD("Dolar", "$"),
    EUR("Euro", "€"),
    TL("TL", "₺");

    private String label;
    private String symbol;

    MoneyTypes(String label, String symbol) {
        this.label = label;
        this.symbol = symbol;
    }
}
