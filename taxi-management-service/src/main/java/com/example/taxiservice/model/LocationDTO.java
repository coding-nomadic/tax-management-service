package com.example.taxiservice.model;

import lombok.Data;

@Data
public class LocationDTO {
    private Double latitude;

    private Double longitude;

    private String name;
}
