package com.parkgrid.service;

import com.parkgrid.domain.Booking;
import com.parkgrid.repo.BookingRepository;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class BillingService {
    private final BookingRepository bookings;
    public BillingService(BookingRepository bookings) { this.bookings = bookings; }

    public Map<String, Object> quote(Long id) {
        Booking booking = bookings.findById(id).orElseThrow();
        long hours = Math.max(1, (booking.getEndTime().getEpochSecond() -
                booking.getStartTime().getEpochSecond() + 3599) / 3600);
        long rate = booking.getSlot().getZone().getRateCents();
        return Map.of("bookingId", id, "durationHours", hours,
                "ratePerHourCents", rate, "totalCents", hours * rate);
    }

    public long calculate(Booking booking) {
        long hours = Math.max(1, (booking.getEndTime().getEpochSecond() -
                booking.getStartTime().getEpochSecond() + 3599) / 3600);
        return hours * booking.getSlot().getZone().getRateCents();
    }
}
