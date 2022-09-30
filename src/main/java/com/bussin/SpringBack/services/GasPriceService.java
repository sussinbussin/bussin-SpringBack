package com.bussin.SpringBack.services;

import com.bussin.SpringBack.models.GasPrice;
import com.bussin.SpringBack.models.GasPriceKey;
import com.bussin.SpringBack.repositories.GasPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GasPriceService {
    private GasPriceRepository gasPriceRepository;

    @Autowired
    public GasPriceService(GasPriceRepository gasPriceRepository) {
        this.gasPriceRepository = gasPriceRepository;
    }

    public List<GasPrice> getRecentGasPrices() {
        return gasPriceRepository.findRecentPrices();
    }

    public BigDecimal getAvgGasPriceByType(GasPriceKey.GasType gasType) {
        List<GasPrice> gasPrices = gasPriceRepository
                .findAvgGasPriceByType(gasType.name());
        if(gasPrices.isEmpty()) {
            return BigDecimal.ONE;
        }
        return gasPrices.stream()
                .map(GasPrice::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(gasPrices.size()));
    }
}
