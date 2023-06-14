package com.crisado.delivery.exception;

public class BankAccountNotFoundException extends BankAccountException {

    private final String key;

    public BankAccountNotFoundException(Object key) {
        super("Bank Account not found");
        this.key = String.valueOf(key);
    }

    public String getKey() {
        return key;
    }

}
