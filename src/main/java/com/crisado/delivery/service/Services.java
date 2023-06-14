package com.crisado.delivery.service;

import com.crisado.delivery.validator.Validators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Getter
public class Services {

    private final Validators validators;
    private final ModelMapper mapper;

    public <T> void validate(String name, T object, Class<?>... groups) {
        validators.validate(name, object, groups);
    }

    public <D> D map(Object source, Class<D> destinationType) {
        return mapper.map(source, destinationType);
    }

}
