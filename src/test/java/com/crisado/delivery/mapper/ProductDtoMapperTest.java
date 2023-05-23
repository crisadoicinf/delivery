package com.crisado.delivery.mapper;

import com.crisado.delivery.config.MapperConfig;
import com.crisado.delivery.dto.ProductDto;
import com.crisado.delivery.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ProductDtoMapperTest {

    private final ModelMapper mapper = new MapperConfig().getModelMapper();

    @Test
    public void mapProduct() {
        var product = Product.builder()
                .id(1)
                .name("prodcut1")
                .price(10D)
                .build();

        assertThat(mapper.map(product, ProductDto.class))
                .extracting(ProductDto::getId, ProductDto::getName, ProductDto::getPrice)
                .containsExactly(1, "prodcut1", 10D);
    }

}
