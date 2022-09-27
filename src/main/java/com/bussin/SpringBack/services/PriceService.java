package com.bussin.SpringBack.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PriceService {
    public BigDecimal getGasPrice() {
        return BigDecimal.valueOf(3);
    }
}
