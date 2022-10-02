package com.bussin.SpringBack.services;

import com.bussin.SpringBack.exception.CannotConnectException;
import com.bussin.SpringBack.models.GasPriceKey;
import com.bussin.SpringBack.models.PlannedRoute;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;

@Service
public class PricingService {
    private final GeoApiContext geoApiContext;
    private final GasPriceService gasPriceService;
    @Autowired
    public PricingService(GeoApiContext geoApiContext,
                          GasPriceService gasPriceService) {
        this.geoApiContext = geoApiContext;
        this.gasPriceService = gasPriceService;
    }

    @Value("${fuelPriceCoefficient}")
    private BigDecimal fuelPriceCoefficient;

    public BigDecimal getPriceOfRide(PlannedRoute plannedRoute){
        try{
            BigDecimal gasPrice =  gasPriceService.getAvgGasPriceByType(
                    GasPriceKey.GasType.valueOf(plannedRoute.getDriver().getFuelType()));

            DistanceMatrix distanceMatrix =
                    DistanceMatrixApi.getDistanceMatrix(geoApiContext,
                            new String[]{plannedRoute.getPlannedFrom()},
                            new String[]{plannedRoute.getPlannedTo()}).await();

            return BigDecimal.valueOf(distanceMatrix.rows[0].elements[0].duration.inSeconds)
                    .multiply(gasPrice).multiply(fuelPriceCoefficient);
        } catch (IOException | ApiException | InterruptedException e) {
            throw new CannotConnectException(e);
        }
    }
}
