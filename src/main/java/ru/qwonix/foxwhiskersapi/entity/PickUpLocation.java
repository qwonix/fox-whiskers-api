package ru.qwonix.foxwhiskersapi.entity;

import javax.persistence.*;

@Entity
@Table(name = "pick_up_location")
public class PickUpLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "city_name")
    private String cityName;

    @Column(name = "street_name")
    private String streetName;

    @Column(name = "house_data")
    private String houseData;

    @Column(name = "additional_information")
    private String additionalInformation;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "priority")
    private Integer priority;
}
