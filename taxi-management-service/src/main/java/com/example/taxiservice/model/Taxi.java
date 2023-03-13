package com.example.taxiservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Taxi")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Taxi {

    @Id
    private String taxiId;

    private TaxiType taxiType;

    private TaxiStatus taxiStatus;
}
