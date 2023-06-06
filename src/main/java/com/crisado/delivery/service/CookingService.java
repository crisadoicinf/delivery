package com.crisado.delivery.service;

import com.crisado.delivery.dto.DateRangeDto;
import java.util.List;
import org.springframework.stereotype.Service;

import com.crisado.delivery.model.CookingProduct;
import com.crisado.delivery.repository.OrderItemRepository;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import com.crisado.delivery.validator.DateRange;
import com.crisado.delivery.validator.Validators;

@Service
@AllArgsConstructor
public class CookingService {

    private final OrderItemRepository orderItemRepository;
    private final Validators validator;

    /**
     * Retrieves a list of CookingProducts that need to be cooked within the
     * specified date range.
     *
     * DateRangeDto must be not null and 'to' date is after the 'from' date.
     *
     * @param dateRange The DateRangeDto representing the desired date range.
     * @return A list of CookingProducts to be cooked within the specified date
     * range.
     */
    public List<CookingProduct> getProductsToCook(DateRangeDto dateRange) {
        validator.validate("dateRange has invalid values", dateRange);
        return orderItemRepository.findAllByDeliveryDate(dateRange.getFrom(), dateRange.getTo());
    }

}
