package io.codelex.flightplanner.flight.controller;

import io.codelex.flightplanner.flight.domain.Flight;
import io.codelex.flightplanner.flight.dto.AddFlightRequest;
import io.codelex.flightplanner.flight.service.FlightService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Validated
@RequestMapping("/admin-api")
public class AdminController {

    private final FlightService flightService;

    public AdminController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PutMapping("/flights")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Flight addFlightRequest(@Valid @RequestBody AddFlightRequest addFlightRequest) {
        return flightService.addFlightRequest(addFlightRequest);
    }

    @DeleteMapping("/flights/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public void deleteFlight(@PathVariable Long id) {
        this.flightService.deleteFlight(id);
    }

    @GetMapping("/flights/{id}")
    public Flight getFlight(@PathVariable Long id) {
        return this.flightService.findFlight(id);
    }

}
