package com.example.busticketbooking.shared.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;

/**
 * Service for handling date and time operations.
 */
@Service
@RequiredArgsConstructor
public class DateTimeService {
    private final Clock clock;

    /**
     * Returns the current UTC time as an Instant.
     *
     * @return the current UTC time
     */
    public Instant getCurrentUtcTime() {
        return Instant.now(clock);
    }

    /**
     * Converts the given UTC time to a ZonedDateTime in the specified time zone.
     *
     * @param utcTime the UTC time to convert
     * @param zoneId  the ID of the time zone to convert to
     * @return the ZonedDateTime in the specified time zone
     */
    public ZonedDateTime convertToZone(Instant utcTime, String zoneId) {
        return utcTime.atZone(ZoneId.of(zoneId));
    }

    /**
     * Converts the given ZonedDateTime to UTC.
     *
     * @param zonedDateTime the ZonedDateTime to convert
     * @return the Instant representing the time in UTC
     */
    public Instant convertToUtc(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toInstant();
    }

    /**
     * Adds a duration in minutes to the given UTC time.
     *
     * @param utcTime  the UTC time to which the duration will be added
     * @param duration the duration to add
     * @return the new Instant after adding the duration
     */
    public Instant addDurationToUtc(Instant utcTime, Duration duration) {
        return utcTime.plusSeconds(duration.toSeconds());
    }
}
