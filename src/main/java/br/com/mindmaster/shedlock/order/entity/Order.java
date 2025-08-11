package br.com.mindmaster.shedlock.order.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime processedAt;

    public Order() {
        createdAt = LocalDateTime.now();
        status = OrderStatus.PENDING;
    }

    public void process() {
        this.status = OrderStatus.PROCESSED;
        this.processedAt = LocalDateTime.now();
    }

}
