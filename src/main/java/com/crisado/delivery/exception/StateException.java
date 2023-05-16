package com.crisado.delivery.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class StateException extends ServiceException {

    public StateException(String message) {
        super(message);
    }

}
