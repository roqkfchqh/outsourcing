package com.example.outsourcing.domain.shop.Mapper;

import com.example.outsourcing.domain.shop.dto.MenuResponseDto;
import com.example.outsourcing.domain.shop.entity.Menu;
import org.springframework.stereotype.Component;

@Component
public class MenuMapper {

    public MenuResponseDto toResponseDto(Menu menu) {
        return new MenuResponseDto(
            menu.getId(),
            menu.getName(),
            menu.getDescription(),
            menu.getPrice(),
            menu.getShop().getId()
        );
    }
}
