package com.example.outsourcing.domain.cart.repository;

import com.example.outsourcing.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

}
