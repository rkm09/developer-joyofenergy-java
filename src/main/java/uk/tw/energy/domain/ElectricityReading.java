package uk.tw.energy.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record ElectricityReading(Instant time, BigDecimal reading) {

}
