package com.crisado.delivery.repository;

import com.crisado.delivery.model.CookingProduct;
import com.crisado.delivery.model.OrderItem;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query(value = """
             select new com.crisado.delivery.model.CookingProduct(p.name, i.note, sum(i.quantity))
             from Order as o
             join o.delivery d
             join o.items i
             join i.product p
             where o.delivery.date between :from and :to
             group by p.name, i.note
            """)
    List<CookingProduct> findAllByDeliveryDate(@Param("from") ZonedDateTime from, @Param("to") ZonedDateTime to);

}