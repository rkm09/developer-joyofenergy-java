package uk.tw.energy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.service.MeterReadingService;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/readings")
public class MeterReadingController {

    @Autowired
    private MeterReadingService meterReadingService;

    @PostMapping("/store")
    public ResponseEntity<String> storeReading(@RequestBody MeterReadings meterReadings) {
        if(!isMeterReadingValid(meterReadings))
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
        meterReadingService.storeReading(meterReadings.smartMeterId(), meterReadings.electricityReadings());
        return ResponseEntity.ok().build();
    }

    private boolean isMeterReadingValid(MeterReadings meterReadings) {
        String smartMeterId = meterReadings.smartMeterId();
        List<ElectricityReading> electricityReadings = meterReadings.electricityReadings();
        return smartMeterId != null &&
                !smartMeterId.isEmpty() &&
                electricityReadings != null &&
                !electricityReadings.isEmpty();
    }

    @GetMapping("/read/{smartMeterId}")
    public ResponseEntity<List<ElectricityReading>> getReadings(@PathVariable("smartMeterId") String smartMeterId) {
        Optional<List<ElectricityReading>> readings = meterReadingService.getReading(smartMeterId);
        return readings.map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());
    }
}
