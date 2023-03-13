package com.example.taxiservice.services;

import com.example.taxiservice.config.RedisConfig;
import com.example.taxiservice.exception.TaxiNotFoundException;
import com.example.taxiservice.model.TaxiBookedEventDTO;
import com.example.taxiservice.model.TaxiBooking;
import com.example.taxiservice.model.TaxiBookingAcceptedEventDTO;
import com.example.taxiservice.model.TaxiBookingCanceledEventDTO;
import com.example.taxiservice.model.TaxiBookingStatus;
import com.example.taxiservice.repository.TaxiBookingRepository;
import com.example.taxiservice.utils.LocationToPointConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

import reactor.core.publisher.Mono;

@Service
public class TaxiBookingServiceImpl implements TaxiBookingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaxiBookingServiceImpl.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final TaxiBookingRepository taxiBookingRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final LocationToPointConverter locationToPointConverter = new LocationToPointConverter();

    public TaxiBookingServiceImpl(RedisTemplate<String, String> redisTemplate,
                                    ReactiveRedisTemplate<String, String> reactiveRedisTemplate,
                                    TaxiBookingRepository taxiBookingRepository) {
        this.redisTemplate = redisTemplate;
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.taxiBookingRepository = taxiBookingRepository;
    }

    @Override
    public Mono<TaxiBooking> book(TaxiBookedEventDTO taxiBookedEventDTO) {
        TaxiBooking taxiBooking = new TaxiBooking();
        taxiBooking.setEnd(locationToPointConverter.convert(taxiBookedEventDTO.getEnd()));
        taxiBooking.setStart(locationToPointConverter.convert(taxiBookedEventDTO.getStart()));
        taxiBooking.setBookedTime(taxiBookedEventDTO.getBookedTime());
        taxiBooking.setCustomerId(taxiBookedEventDTO.getCustomerId());
        taxiBooking.setBookingStatus(TaxiBookingStatus.ACTIVE);
        TaxiBooking savedTaxiBooking = taxiBookingRepository.save(taxiBooking);
        return Mono.just(savedTaxiBooking);
    }

    @Override
    public Mono<TaxiBooking> accept(String taxiBookingId, TaxiBookingAcceptedEventDTO acceptedEventDTO) {
        Optional<TaxiBooking> taxiBookingOptional = taxiBookingRepository.findById(taxiBookingId);
        if (taxiBookingOptional.isPresent()) {
            TaxiBooking taxiBooking = taxiBookingOptional.get();
            taxiBooking.setTaxiId(acceptedEventDTO.getTaxiId());
            taxiBooking.setAcceptedTime(acceptedEventDTO.getAcceptedTime());
            return Mono.just(taxiBookingRepository.save(taxiBooking)).doOnSuccess(t -> {
                try {
                    redisTemplate.convertAndSend(RedisConfig.ACCEPTED_EVENT_CHANNEL,
                                                    objectMapper.writeValueAsString(acceptedEventDTO));
                } catch (JsonProcessingException e) {
                    LOGGER.error("Error while sending message to Channel {}", RedisConfig.ACCEPTED_EVENT_CHANNEL, e);
                }
            });
        } else {
            throw new TaxiNotFoundException("Taxi Booking Not Found for : {}" + taxiBookingId, "101");
        }
    }

    @Override
    public Mono<TaxiBooking> updateBookingStatus(String taxiBookingId, TaxiBookingStatus taxiBookingStatus) {
        Optional<TaxiBooking> taxiBookingOptional = taxiBookingRepository.findById(taxiBookingId);
        if (taxiBookingOptional.isPresent()) {
            TaxiBooking taxiBooking = taxiBookingOptional.get();
            taxiBooking.setBookingStatus(taxiBookingStatus);
            return Mono.just(taxiBookingRepository.save(taxiBooking));
        } else {
            throw new TaxiNotFoundException("Taxi Booking Not Found for {}" + taxiBookingId, "101");
        }
    }

    @Override
    public Mono<TaxiBooking> cancel(String taxiBookingId, TaxiBookingCanceledEventDTO canceledEventDTO) {
        Optional<TaxiBooking> taxiBookingOptional = taxiBookingRepository.findById(taxiBookingId);
        if (taxiBookingOptional.isPresent()) {
            TaxiBooking taxiBooking = taxiBookingOptional.get();
            taxiBooking.setBookingStatus(TaxiBookingStatus.CANCELLED);
            taxiBooking.setReasonToCancel(canceledEventDTO.getReason());
            taxiBooking.setCancelTime(canceledEventDTO.getCancelTime());
            return Mono.just(taxiBookingRepository.save(taxiBooking));
        } else {
            throw new TaxiNotFoundException("Taxi Booking Not Found for {}" + taxiBookingId, "101");
        }
    }

}
