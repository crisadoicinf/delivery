package com.crisado.delivery.exception;

public class ProductNotFoundException extends ProductException {

    private final String key;

    public ProductNotFoundException(Object key) {
        super("Product not found");
        this.key = String.valueOf(key);
    }

    public String getKey() {
        return key;
    }

}
