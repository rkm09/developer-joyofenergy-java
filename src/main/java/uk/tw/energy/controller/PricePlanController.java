package uk.tw.energy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.PricePlanService;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/price-plans")
public class PricePlanController {
    @Autowired
    private PricePlanService pricePlanService;
    @Autowired
    private AccountService accountService;
    private static final String PRICE_PLAN_ID_KEY = "pricePlanId";
    private static final String PRICE_PLAN_COMPARISON_KEY = "pricePlanComparisons";

    @GetMapping("/compare-all/{smartMeterId}")
    public ResponseEntity<Map<String, Object>> calculatedCostForEachPricePlan(@PathVariable("smartMeterId") String smartMeterId) {
        String pricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);
        Optional<Map<String, BigDecimal>> consumptionForPricePlans =
                pricePlanService.getConsumptionCostOfElectricityReadingForEachPricePlan(smartMeterId);
        if(consumptionForPricePlans.isEmpty())
            return ResponseEntity.notFound().build();
        Map<String, Object> pricePlanComparisons = new HashMap<>();
        pricePlanComparisons.put(PRICE_PLAN_ID_KEY, pricePlanId);
        pricePlanComparisons.put(PRICE_PLAN_COMPARISON_KEY, consumptionForPricePlans.get());
        return ResponseEntity.ok(pricePlanComparisons);
    }

    @GetMapping("/recommend/{smartMeterId}")
    public ResponseEntity<List<Map.Entry<String, BigDecimal>>> recommendCheapestPricePlans(
            @PathVariable("smartMeterId") String smartMeterId, @RequestParam(value = "limit", required = false) Integer limit
    ) {
        Optional<Map<String, BigDecimal>> consumptionForPricePlans =
                pricePlanService.getConsumptionCostOfElectricityReadingForEachPricePlan(smartMeterId);
        if(consumptionForPricePlans.isEmpty())
            return ResponseEntity.notFound().build();
        List<Map.Entry<String, BigDecimal>> recommendations =
                new ArrayList<>(consumptionForPricePlans.get().entrySet());
        recommendations.sort(Map.Entry.comparingByValue());
        if(limit != null && limit < recommendations.size())
            recommendations = recommendations.subList(0, limit);
        return ResponseEntity.ok(recommendations);
    }
}
