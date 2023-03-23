package ru.qwonix.foxwhiskersapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "dish_details")
public class DishDetails {
    // FIXME: 22-Mar-23 remove GenerationType.SEQUENCE
    /**
     * Using a {@link GenerationType#SEQUENCE} strategy can cause the uniqueness of the primary key to be violated
     * if several clients connect to the database
     */
    @Id
    @SequenceGenerator(name = "dish_details_seq",
            sequenceName = "pet_sequence",
            allocationSize = 20)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dish_details_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "composition_text", nullable = false)
    private String compositionText;

    @Column(name = "image_name")
    private String imageName;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "image_data_id")
    private ImageData imageData;
}
