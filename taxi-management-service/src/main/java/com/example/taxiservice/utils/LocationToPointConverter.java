package com.example.taxiservice.utils;

import com.example.taxiservice.model.LocationDTO;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.geo.Point;
import org.springframework.lang.Nullable;

public class LocationToPointConverter implements Converter<LocationDTO, Point> {

    @Nullable
    @Override
    public Point convert(LocationDTO locationDTO) {
        if (locationDTO == null) {
            return null;
        }
        return new Point(locationDTO.getLongitude(), locationDTO.getLatitude());
    }
}