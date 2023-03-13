package com.example.taxiservice.model;


import lombok.Data;

@Data
public class TaxiRegisterEventDTO {

    private String taxiId;

    private TaxiType taxiType;

    private TaxiStatus taxiStatus;
}
