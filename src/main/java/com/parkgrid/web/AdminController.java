package com.parkgrid.web;
import com.parkgrid.repo.BookingRepository;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.web.bind.annotation.*;import java.util.Map;
@RestController @RequestMapping("/api/admin") @PreAuthorize("hasRole('ADMIN')")
public class AdminController {
 private final BookingRepository bookings;public AdminController(BookingRepository b){bookings=b;}
 @GetMapping("/metrics") public Map<String,Object> metrics(){var all=bookings.findAll();return Map.of("totalBookings",all.size(),"confirmedBookings",all.stream().filter(b->b.getStatus().name().equals("CONFIRMED")).count(),"revenueCents",all.stream().mapToLong(b->b.getAmountCents()).sum());}
}
