package com.example.taxiservice.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxiBookingCanceledEventDTO {

    private String taxiBookingId;

    private String reason;

    private Date cancelTime = new Date();

}