package com.example.taxiservice.services;

import com.example.taxiservice.model.TaxiBookedEventDTO;
import com.example.taxiservice.model.TaxiBooking;
import com.example.taxiservice.model.TaxiBookingAcceptedEventDTO;
import com.example.taxiservice.model.TaxiBookingCanceledEventDTO;
import com.example.taxiservice.model.TaxiBookingStatus;

import reactor.core.publisher.Mono;

public interface TaxiBookingService {

    /**
     * 
     * @param taxiBookedEventDTO
     * @return
     */
    public Mono<TaxiBooking> book(TaxiBookedEventDTO taxiBookedEventDTO);

    /**
     * 
     * @param taxiBookingId
     * @param acceptedEventDTO
     * @return
     */
    public Mono<TaxiBooking> accept(String taxiBookingId, TaxiBookingAcceptedEventDTO acceptedEventDTO);

    /**
     * 
     * @param taxiBookingId
     * @param taxiBookingStatus
     * @return
     */
    public Mono<TaxiBooking> updateBookingStatus(String taxiBookingId, TaxiBookingStatus taxiBookingStatus);

    /**
     * 
     * @param taxiBookingId
     * @param canceledEventDTO
     * @return
     */
    public Mono<TaxiBooking> cancel(String taxiBookingId, TaxiBookingCanceledEventDTO canceledEventDTO);
}
