package com.crisado.delivery.repository;

import com.crisado.delivery.model.Order;
import com.crisado.delivery.model.OrdersCountByDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByDeliveryDateBetween(ZonedDateTime from, ZonedDateTime to);

    @Query(value = """
             select new com.crisado.delivery.model.OrdersCountByDay(day(o.delivery.date), count(o.id))
             from Order as o
             where month(o.delivery.date) = :month
             group by day(o.delivery.date)
             order by day(o.delivery.date) asc
            """)
    List<OrdersCountByDay> countTotalByDeliveryMonthDay(int month);


}