package com.crisado.delivery.exception;

public class RiderNotFoundException extends RiderException {

    private final String key;

    public RiderNotFoundException(Object key) {
        super("Rider not found");
        this.key = String.valueOf(key);
    }

    public String getKey() {
        return key;
    }

}
