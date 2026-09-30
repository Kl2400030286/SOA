package com.parkgrid.web;

import com.parkgrid.service.BillingService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/internal/billing")
public class InternalBillingController {
    private final BillingService billing;

    public InternalBillingController(BillingService billing) {
        this.billing = billing;
    }

    @GetMapping("/bookings/{id}/quote")
    public Map<String,Object> quote(@PathVariable Long id) {
        return billing.quote(id);
    }
}
