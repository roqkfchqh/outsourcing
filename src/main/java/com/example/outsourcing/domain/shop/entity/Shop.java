package com.example.outsourcing.domain.shop.entity;

import com.example.outsourcing.domain.common.entity.Timestamped;
import com.example.outsourcing.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "shops")
@NoArgsConstructor
public class Shop extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String name;
    private BigDecimal minOrderPrice = BigDecimal.ZERO;
    private LocalTime open;
    private LocalTime close;
    private boolean isDeleted;

    public Shop(Long id, String name, BigDecimal minOrderPrice, LocalTime open, LocalTime close,
        boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.minOrderPrice = minOrderPrice;
        this.open = open;
        this.close = close;
        this.isDeleted = isDeleted;
    }
}
