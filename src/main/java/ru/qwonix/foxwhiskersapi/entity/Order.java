package ru.qwonix.foxwhiskersapi.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "`order`")
@Data
public class Order {

    // FIXME: 23-Mar-23 remove GenerationType.SEQUENCE
    /**
     * Using a {@link GenerationType#SEQUENCE} strategy can cause the uniqueness of the primary key to be violated
     * if several clients connect to the database
     */
    @Id
    @SequenceGenerator(name = "order_seq",
            sequenceName = "order_sequence",
            allocationSize = 20)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @CreatedDate
    @Column(name = "created")
    private LocalDateTime created;

    @LastModifiedDate
    @Column(name = "updated")
    private LocalDateTime updated;
}
