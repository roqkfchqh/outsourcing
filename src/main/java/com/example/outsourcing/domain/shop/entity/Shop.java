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

    // 커스텀 생성자 추가
    public Shop(User user, String name, BigDecimal minOrderPrice) {
        this.user = user;
        this.name = name;
        this.minOrderPrice = minOrderPrice;
    }

    // 소프트 딜리트 메서드 추가
    public void markAsDeleted() {
        this.isDeleted = true;
    }

    //업데이트 메서드 추가
    public void update(String name, BigDecimal minOrderPrice) {
        this.name = name;
        this.minOrderPrice = minOrderPrice;
    }
}
