package com.example.taxiservice.services;

import com.example.taxiservice.model.*;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.redis.connection.RedisGeoCommands;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaxiService {

    /**
     * @param taxiRegisterEventDTO
     * @return
     */
    public Mono<Taxi> register(TaxiRegisterEventDTO taxiRegisterEventDTO);

    /**
     * @param taxiId
     * @param locationDTO
     * @return
     */
    public Mono<Taxi> updateLocation(String taxiId, LocationDTO locationDTO);

    /**
     * @param taxiId
     * @return
     */
    public Mono<TaxiStatus> getTaxiStatus(String taxiId);


    /**
     * @param taxiId
     * @param taxiStatus
     * @return
     */
    public Mono<Taxi> updateTaxiStatus(String taxiId, TaxiStatus taxiStatus);

    /**
     * @param taxiType
     * @param latitude
     * @param longitude
     * @param radius
     * @return
     */
    public Flux<GeoResult<RedisGeoCommands.GeoLocation<String>>> getAvailableTaxis(TaxiType taxiType, Double latitude, Double longitude, Double radius);
}
