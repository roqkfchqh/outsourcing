package com.example.outsourcing.domain.order.repository;

import com.example.outsourcing.domain.order.entity.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o " +
        "JOIN o.orderMenus om " +
        "JOIN om.menu m " +
        "JOIN m.shop s " +
        "JOIN s.user u " +
        "WHERE o.id = :orderId AND u.id = :ownerId")
    Optional<Order> findOrderByOwner(@Param("orderId") Long orderId,
        @Param("ownerId") Long ownerId);

    @Query("SELECT DISTINCT o FROM Order o " +
        "JOIN o.orderMenus om " +
        "JOIN om.menu m " +
        "WHERE m.shop.id = :shopId")
    List<Order> findAllByShopId(@Param("shopId") Long shopId);
}
