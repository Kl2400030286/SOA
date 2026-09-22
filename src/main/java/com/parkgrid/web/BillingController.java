package com.parkgrid.web;
import com.parkgrid.domain.Booking;import com.parkgrid.repo.BookingRepository;import org.springframework.web.bind.annotation.*;import java.time.*;import java.util.Map;
@RestController @RequestMapping("/api/billing")
public class BillingController {
 private final BookingRepository bookings;public BillingController(BookingRepository b){bookings=b;}
 @GetMapping("/bookings/{id}/quote") public Map<String,Object> quote(@PathVariable Long id){Booking b=bookings.findById(id).orElseThrow();long hours=Math.max(1,(b.getEndTime().getEpochSecond()-b.getStartTime().getEpochSecond()+3599)/3600);long rate=b.getSlot().getZone().getRateCents();return Map.of("bookingId",id,"durationHours",hours,"ratePerHourCents",rate,"totalCents",hours*rate);}
}
