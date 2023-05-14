package ru.qwonix.foxwhiskersapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "`order`")
public class Order {

    // FIXME: 23-Mar-23 remove GenerationType.SEQUENCE
    /**
     * Using a {@link GenerationType#SEQUENCE} strategy can cause the uniqueness of the primary key to be violated
     * if several clients connect to the database
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderItem> orderItems;

    @CreatedDate
    @Column(name = "created")
    private LocalDateTime created;

    @LastModifiedDate
    @Column(name = "updated")
    private LocalDateTime updated;
}
