package com.example.taxiservice.controllers;

import com.example.taxiservice.model.Taxi;
import com.example.taxiservice.model.TaxiRegisterEventDTO;
import com.example.taxiservice.model.TaxiStatus;
import com.example.taxiservice.model.TaxiStatusDTO;
import com.example.taxiservice.services.TaxiService;
import com.example.taxiservice.utils.JsonUtils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequestMapping("/taxis")
@RestController
public class TaxiServiceController {

    private final TaxiService taxiService;

    public TaxiServiceController(TaxiService taxiService) {
        this.taxiService = taxiService;
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Mono<Taxi> register(@RequestBody TaxiRegisterEventDTO taxiRegisterEventDTO) throws IOException {
        log.info("Register Request : {}", JsonUtils.toString(taxiRegisterEventDTO));
        return taxiService.register(taxiRegisterEventDTO);
    }

    @GetMapping("/{taxiId}/status")
    public Mono<TaxiStatusDTO> getTaxiStatus(@PathVariable("taxiId") String taxiId) {
        log.info("Taxi Status for Taxi ID : {}", taxiId);
        return taxiService.getTaxiStatus(taxiId);
    }

    @PutMapping("/{taxiId}/status")
    public Mono<TaxiStatusDTO> updateTaxiStatus(@PathVariable("taxiId") String taxiId,
                                    @RequestParam("status") TaxiStatus taxiStatus) {
        log.info("Taxi Status Update for Taxi ID : {}", taxiId);
        return taxiService.updateTaxiStatus(taxiId, taxiStatus)
                                        .map(t -> new TaxiStatusDTO(t.getTaxiId(), t.getTaxiStatus()));
    }
}