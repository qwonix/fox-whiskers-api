package ru.qwonix.foxwhiskersapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Data
@ToString(exclude = "dish")
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "dish_details")
public class DishDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "composition_text", nullable = false)
    private String compositionText;

    @Column(name = "measure_text", nullable = false)
    private String measureText;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "image_data_id")
    private ImageData imageData;
}
