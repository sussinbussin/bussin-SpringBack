package com.bussin.SpringBack.repositories;

import com.bussin.SpringBack.models.GasPrice;
import com.bussin.SpringBack.models.GasPriceKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GasPriceRepository extends JpaRepository<GasPrice, GasPriceKey> {
    @Query(value = "SELECT g.* FROM gas_price g INNER JOIN ( SELECT company, "
            + "MAX(date_time) AS latest FROM gas_price GROUP BY company) AS "
            + "grouped "
            + "ON grouped.company = g.company AND grouped.latest = g"
            + ".date_time;", nativeQuery = true)
    List<GasPrice> findRecentPrices();

    @Query(value = "SELECT g.* FROM gas_price g INNER JOIN ( SELECT company, "
            + "MAX(date_time) AS latest FROM gas_price GROUP BY company) AS "
            + "grouped "
            + "ON grouped.company = g.company AND grouped.latest = g"
            + ".date_time WHERE gas_type LIKE :gasType ;", nativeQuery = true)
    List<GasPrice> findAvgGasPriceByType(String gasType);
}
