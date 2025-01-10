package com.example.outsourcing.domain.shop.repository;

import com.example.outsourcing.domain.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> ,  ShopRepositoryCustom {

    long countByUserIdAndIsDeletedFalse(Long userId);

}
