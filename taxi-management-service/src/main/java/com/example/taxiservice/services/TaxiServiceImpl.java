package com.example.taxiservice.services;

import com.example.taxiservice.exception.TaxiNotFoundException;
import com.example.taxiservice.model.*;
import com.example.taxiservice.repository.TaxiRepository;
import com.example.taxiservice.utils.LocationToPointConverter;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class TaxiServiceImpl implements TaxiService {
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final TaxiRepository taxiRepository;
    private final LocationToPointConverter locationToPointConverter = new LocationToPointConverter();

    public TaxiServiceImpl(ReactiveRedisTemplate<String, String> reactiveRedisTemplate, TaxiRepository taxiRepository) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.taxiRepository = taxiRepository;
    }

    @Override
    public Mono<Taxi> register(TaxiRegisterEventDTO taxiRegisterEventDTO) {
        Taxi taxi = new Taxi(taxiRegisterEventDTO.getTaxiId(), taxiRegisterEventDTO.getTaxiType(), TaxiStatus.AVAILABLE);
        return Mono.just(taxiRepository.save(taxi));
    }

    @Override
    public Mono<Taxi> updateLocation(String taxiId, LocationDTO locationDTO) {
        Optional<Taxi> taxiOptional = taxiRepository.findById(taxiId);
        if (taxiOptional.isPresent()) {
            Taxi taxi = taxiOptional.get();
            return reactiveRedisTemplate.opsForGeo().add(taxi.getTaxiType().toString(), locationToPointConverter.convert(locationDTO), taxiId.toString()).flatMap(l -> Mono.just(taxi));
        } else {
            throw new TaxiNotFoundException("Taxi Not Found : " + taxiId, "101");
        }
    }

    @Override
    public Mono<TaxiStatus> getTaxiStatus(String taxiId) {
        Optional<Taxi> taxiStatus = taxiRepository.findById(taxiId);
        return taxiStatus.isPresent() ? Mono.just(taxiStatus.get().getTaxiStatus()) : Mono.just(null);
    }

    @Override
    public Mono<Taxi> updateTaxiStatus(String taxiId, TaxiStatus taxiStatus) {
        Optional<Taxi> taxiRepo = taxiRepository.findById(taxiId);
        if (taxiRepo.isPresent()) {
            Taxi taxi = taxiRepo.get();
            taxi.setTaxiStatus(taxiStatus);
            return Mono.just(taxiRepository.save(taxi));
        } else {
            throw new TaxiNotFoundException("Taxi Not Found : " + taxiId, "101");
        }
    }

    @Override
    public Flux<GeoResult<RedisGeoCommands.GeoLocation<String>>> getAvailableTaxis(TaxiType taxiType, Double latitude, Double longitude, Double radius) {
        return reactiveRedisTemplate.opsForGeo().radius(taxiType.toString(), new Circle(new Point(longitude, latitude), new Distance(radius, Metrics.KILOMETERS)));
    }
}
