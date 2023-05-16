package com.crisado.delivery.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceNotFoundException extends ServiceException {

    public ResourceNotFoundException(String resource) {
        super(String.format("%s not found"));
    }

}
