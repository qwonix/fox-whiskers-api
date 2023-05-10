package ru.qwonix.foxwhiskersapi.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "dish")
public class Dish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "currency_price", nullable = false)
    private BigDecimal currencyPrice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dish_type_id")
    private DishType type;

    @OneToOne(mappedBy = "dish", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DishDetails dishDetails;

    @ColumnDefault("true")
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;
}

