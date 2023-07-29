package ru.qwonix.foxwhiskersapi.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PickUpLocation {

    private Long id;
    private String title;
    private String cityName;
    private String streetName;
    private String houseData;
    private String additionalInformation;
    private Double latitude;
    private Double longitude;
    private Integer priority;
}
