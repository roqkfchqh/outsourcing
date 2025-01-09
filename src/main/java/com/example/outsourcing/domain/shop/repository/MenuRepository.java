package com.example.outsourcing.domain.shop.repository;

import com.example.outsourcing.domain.shop.entity.Menu;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByIdIn(List<Long> ids);
}
