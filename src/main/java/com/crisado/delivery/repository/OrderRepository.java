package com.crisado.delivery.repository;

import com.crisado.delivery.model.Order;
import com.crisado.delivery.model.OrdersCountByDay;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface OrderRepository extends Repository<Order, Long> {

    Optional<Order> findById(Long id);

    List<Order> findAllByDeliveryDateBetween(ZonedDateTime from, ZonedDateTime to);

    @Query(value = """
             select o, d, r
             from Order as o
             join o.delivery d
             left join d.rider r
             where o.delivery.date between :from and :to
             and (o.delivery.rider.id is null or o.delivery.rider.id = :riderId)
            """)
    List<Order> findAllByDeliveryDateBetweenAndOptionalRiderId(
            @Param("from") ZonedDateTime from,
            @Param("to") ZonedDateTime to,
            @Param("riderId") int riderId
    );

    @Query(value = """
             select new com.crisado.delivery.model.OrdersCountByDay(day(o.delivery.date), count(o.id))
             from Order as o
             where month(o.delivery.date) = :month
             group by day(o.delivery.date)
             order by day(o.delivery.date) asc
            """)
    List<OrdersCountByDay> countTotalByDeliveryMonthDay(int month);

    void save(Order order);

    void deleteById(long id);

}
