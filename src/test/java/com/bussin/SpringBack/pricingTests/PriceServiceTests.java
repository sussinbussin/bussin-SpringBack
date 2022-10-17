package com.bussin.SpringBack.pricingTests;

import com.bussin.SpringBack.services.GasPriceService;
import com.bussin.SpringBack.services.PricingService;
import com.google.maps.GeoApiContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PriceServiceTests {
    @Mock
    GeoApiContext geoApiContext;

    @Mock
    GasPriceService gasPriceService;

    @InjectMocks
    private PricingService pricingService;

    @BeforeEach
    private void setUp() {
        pricingService = new PricingService(geoApiContext, gasPriceService);
        pricingService.setFuelPriceCoefficient(BigDecimal.valueOf(0.165));
    }

    @Test
    public void calculatePrice_success() {
        assertEquals(BigDecimal.valueOf(0.495),
                pricingService.calculatePrice(1000, BigDecimal.valueOf(3)));
    }
}
