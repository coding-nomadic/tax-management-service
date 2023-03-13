package com.example.taxiservice.services;

import com.example.taxiservice.exception.TaxiNotFoundException;
import com.example.taxiservice.model.LocationDTO;
import com.example.taxiservice.model.Taxi;
import com.example.taxiservice.model.TaxiRegisterEventDTO;
import com.example.taxiservice.model.TaxiStatus;
import com.example.taxiservice.model.TaxiStatusDTO;
import com.example.taxiservice.model.TaxiType;
import com.example.taxiservice.repository.TaxiRepository;
import com.example.taxiservice.utils.LocationToPointConverter;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
        Taxi taxi = new Taxi(taxiRegisterEventDTO.getTaxiId(), taxiRegisterEventDTO.getTaxiType(),
                                        TaxiStatus.AVAILABLE);
        return Mono.just(taxiRepository.save(taxi));
    }

    @Override
    public Mono<Taxi> updateLocation(String taxiId, LocationDTO locationDTO) {
        Optional<Taxi> taxiOptional = taxiRepository.findById(taxiId);
        if (taxiOptional.isPresent()) {
            Taxi taxi = taxiOptional.get();
            return reactiveRedisTemplate.opsForGeo()
                                            .add(taxi.getTaxiType().toString(), locationToPointConverter
                                                                            .convert(locationDTO), taxiId.toString())
                                            .flatMap(l -> Mono.just(taxi));
        } else {
            throw new TaxiNotFoundException("Taxi Not Found for {}" + taxiId, "101");
        }
    }

    @Override
    public Mono<TaxiStatusDTO> getTaxiStatus(String taxiId) {
        Optional<Taxi> taxiStatus = taxiRepository.findById(taxiId);
        if (taxiStatus.isPresent()) {
            TaxiStatusDTO taxiStatusDTO = new TaxiStatusDTO();
            taxiStatusDTO.setTaxiId(taxiId);
            taxiStatusDTO.setStatus(taxiStatus.get().getTaxiStatus());
            return Mono.just(taxiStatusDTO);
        } else {
            throw new TaxiNotFoundException("Taxi Not Found for {}" + taxiId, "101");
        }
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
    public Flux<GeoResult<RedisGeoCommands.GeoLocation<String>>> getAvailableTaxis(TaxiType taxiType, Double latitude,
                                    Double longitude, Double radius) {
        return reactiveRedisTemplate.opsForGeo().radius(taxiType.toString(), new Circle(new Point(longitude, latitude),
                                        new Distance(radius, Metrics.KILOMETERS)));
    }
}
