package com.example.taxiservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxiStatusDTO {
    private String taxiId;

    private TaxiStatus status;
}
