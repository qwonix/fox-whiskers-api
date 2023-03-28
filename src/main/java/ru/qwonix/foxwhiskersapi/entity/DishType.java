package ru.qwonix.foxwhiskersapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "dish_type")
public class DishType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @ColumnDefault("true")
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @OneToMany(mappedBy = "dishType")
    private List<Dish> dishes;
}
