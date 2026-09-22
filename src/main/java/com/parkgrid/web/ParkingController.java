package com.parkgrid.web;
import com.parkgrid.domain.*; import com.parkgrid.repo.*; import jakarta.validation.Valid; import jakarta.validation.constraints.*; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/parking")
public class ParkingController {
 private final ParkingZoneRepository zones;private final ParkingSlotRepository slots;
 public ParkingController(ParkingZoneRepository z,ParkingSlotRepository s){zones=z;slots=s;}
 public record ZoneRequest(@NotBlank String name,@NotBlank String address,@Min(1) int capacity,@Min(5) @Max(100) int rateCents){}
 @GetMapping("/zones") public List<ParkingZone> zones(){return zones.findAll();}
 @PostMapping("/zones") @PreAuthorize("hasRole('ADMIN')") public ParkingZone create(@Valid @RequestBody ZoneRequest r){ParkingZone z=zones.save(new ParkingZone(r.name(),r.address(),r.capacity(),r.rateCents()));for(int i=1;i<=r.capacity();i++)slots.save(new ParkingSlot(z,"S-"+i));return z;}
 @GetMapping("/zones/{zoneId}/availability") public Map<String,Object> availability(@PathVariable Long zoneId){ParkingZone z=zones.findById(zoneId).orElseThrow();long available=slots.countByZoneIdAndStatus(zoneId,ParkingSlot.Status.AVAILABLE);return Map.of("zoneId",zoneId,"capacity",z.getCapacity(),"available",available,"occupied",z.getCapacity()-available);}
 @GetMapping("/zones/{zoneId}/slots") public List<ParkingSlot> slots(@PathVariable Long zoneId){return slots.findByZoneId(zoneId);}
}
