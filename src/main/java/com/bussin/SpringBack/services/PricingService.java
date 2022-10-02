package com.bussin.SpringBack.services;

import com.bussin.SpringBack.exception.CannotConnectException;
import com.bussin.SpringBack.models.GasPriceKey;
import com.bussin.SpringBack.models.PlannedRoute;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
    @Setter
    private BigDecimal fuelPriceCoefficient;

    public BigDecimal getPriceOfRide(PlannedRoute plannedRoute){
        try{
            BigDecimal gasPrice =  gasPriceService.getAvgGasPriceByType(
                    GasPriceKey.GasType.valueOf(plannedRoute.getDriver().getFuelType()));

            DistanceMatrix distanceMatrix =
                    DistanceMatrixApi.getDistanceMatrix(geoApiContext,
                            new String[]{plannedRoute.getPlannedFrom()},
                            new String[]{plannedRoute.getPlannedTo()}).await();

            return calculatePrice(distanceMatrix.rows[0].elements[0].distance.inMeters, gasPrice);
        } catch (IOException | ApiException | InterruptedException e) {
            throw new CannotConnectException(e);
        }
    }

    public BigDecimal calculatePrice(long metres, BigDecimal gasPrice) {
        return BigDecimal.valueOf(metres).divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP)
                .multiply(fuelPriceCoefficient).multiply(gasPrice);
    }
}
