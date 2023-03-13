package com.example.taxiservice.controllers;

import com.example.taxiservice.model.TaxiBookedEventDTO;
import com.example.taxiservice.model.TaxiBooking;
import com.example.taxiservice.model.TaxiBookingAcceptedEventDTO;
import com.example.taxiservice.model.TaxiBookingCanceledEventDTO;
import com.example.taxiservice.services.TaxiBookingService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RequestMapping("/taxibookings")
@RestController
public class TaxiBookingServiceController {

    private final TaxiBookingService taxiBookingService;

    public TaxiBookingServiceController(TaxiBookingService taxiBookingService) {
        this.taxiBookingService = taxiBookingService;
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Mono<TaxiBooking> book(@RequestBody TaxiBookedEventDTO taxiBookedEventDTO) {
        return taxiBookingService.book(taxiBookedEventDTO);
    }
    
    @PutMapping("/{taxiBookingId}/cancel")
    public Mono<TaxiBooking> cancel(@PathVariable("taxiBookingId") String taxiBookingId,
                                    @RequestBody TaxiBookingCanceledEventDTO taxiBookingCanceledEventDTO) {
        return taxiBookingService.cancel(taxiBookingId, taxiBookingCanceledEventDTO);
    }
    
    @PutMapping("/{taxiBookingId}/accept")
    public Mono<TaxiBooking> accept(@PathVariable("taxiBookingId") String taxiBookingId,
                                    @RequestBody TaxiBookingAcceptedEventDTO taxiBookingAcceptedEventDTO) {
        return taxiBookingService.accept(taxiBookingId, taxiBookingAcceptedEventDTO);
    }
}
