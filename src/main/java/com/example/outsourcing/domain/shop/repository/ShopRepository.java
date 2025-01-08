package com.example.outsourcing.domain.shop.repository;

import com.example.outsourcing.domain.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    @Query("SELECT m.shop FROM Menu m WHERE m.id = :menuId")
    Shop findByMenuId(@Param("menuId") Long menuId);
}
