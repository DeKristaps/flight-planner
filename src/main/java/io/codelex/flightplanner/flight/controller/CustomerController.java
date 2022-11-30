package io.codelex.flightplanner.flight.controller;

import io.codelex.flightplanner.flight.domain.Airport;
import io.codelex.flightplanner.flight.domain.Flight;
import io.codelex.flightplanner.flight.dto.ResultPage;
import io.codelex.flightplanner.flight.dto.SearchFlightsRequest;
import io.codelex.flightplanner.flight.service.FlightService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@Validated
@RequestMapping("/api")
public class CustomerController {

    private final FlightService flightService;

    public CustomerController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/airports")
    public List<Airport> searchAirports(@RequestParam String search) {
        return this.flightService.getAirport(search);
    }

    @PostMapping("/flights/search")
    public ResultPage searchFlights(@Valid @RequestBody SearchFlightsRequest searchFlightsRequest) {
        return this.flightService.searchFlightsRequest(searchFlightsRequest);
    }

    @GetMapping("/flights/{id}")
    public Flight searchFlightById(@PathVariable Long id) {
        return this.flightService.findFlight(id);
    }

}
