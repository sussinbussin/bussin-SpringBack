package com.bussin.SpringBack.gasPriceTests;

import com.bussin.SpringBack.models.GasPrice;
import com.bussin.SpringBack.models.GasPriceKey;
import com.bussin.SpringBack.repositories.GasPriceRepository;
import com.bussin.SpringBack.services.GasPriceService;
import com.bussin.SpringBack.testConfig.H2JpaConfig;
import com.bussin.SpringBack.testConfig.TestContextConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestContextConfig.class, H2JpaConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GasPriceSQLTests {
    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private GasPriceRepository gasPriceRepository;

    @Autowired
    private GasPriceService gasPriceService;

    private static final List<GasPrice> expectedRecent = new ArrayList<>();

    private void populateTable() {
        List<GasPrice> toSave = new ArrayList<>();
        String[] companies = {"esso", "shell", "spc", "caltex", "sinodec"};
        GasPriceKey.GasType[] gasTypes = {GasPriceKey.GasType.Type92,
                GasPriceKey.GasType.Type95, GasPriceKey.GasType.Type98,
                GasPriceKey.GasType.TypeDiesel,
                GasPriceKey.GasType.TypePremium};
        for (int k = 0; k < 2; k++) {
            for (int j = 0; j < 5; j++) {
                for (int i = 0; i < 5; i++) {
                    GasPriceKey gasPriceKey = GasPriceKey.builder()
                            .gasType(gasTypes[j])
                            .company(companies[i])
                            .dateTime(LocalDateTime.of(2022, 9, 16, 12 - k, 12))
                            .build();
                    GasPrice gasPrice = new GasPrice(gasPriceKey,
                            BigDecimal.valueOf((i + 1) * (j + 1)));
                    toSave.add(gasPrice);
                    if (k == 0) {
                        expectedRecent.add(gasPrice);
                    }
                }
            }
        }

        gasPriceRepository.saveAll(toSave);
    }

    @Test
    public void getRecentGasPrices_success() {
        populateTable();
        assertThat(gasPriceService.getRecentGasPrices()).hasSameElementsAs(expectedRecent);
    }

    @Test
    public void getAvgGasPriceByType_success() {
        populateTable();
        assertEquals(BigDecimal.valueOf(3).setScale(2),
                gasPriceService.getAvgGasPriceByType
                (GasPriceKey.GasType.Type92).setScale(2));
    }
}
