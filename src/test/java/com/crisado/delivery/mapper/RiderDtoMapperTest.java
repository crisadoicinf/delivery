package com.crisado.delivery.mapper;

import com.crisado.delivery.config.MapperConfig;
import com.crisado.delivery.dto.RiderDto;
import com.crisado.delivery.model.Rider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class RiderDtoMapperTest {

    private final ModelMapper mapper = new MapperConfig().getModelMapper();

    @Test
    public void mapRider() {
        var rider = Rider.builder()
                .id(1)
                .name("rider1")
                .build();

        assertThat(mapper.map(rider, RiderDto.class))
                .extracting(RiderDto::getId, RiderDto::getName)
                .containsExactly(1, "rider1");
    }

}
