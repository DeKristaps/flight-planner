package io.codelex.flightplanner.customerClient;

import io.codelex.flightplanner.admin.domain.Airport;
import io.codelex.flightplanner.admin.domain.Flight;
import io.codelex.flightplanner.customerClient.domain.ResultPage;
import io.codelex.flightplanner.customerClient.domain.SearchFlightsRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/airports")
    public List<Airport> searchAirports(@RequestParam String search) {
        return this.customerService.getAirport(search);
    }

    @PostMapping("/flights/search")
    public ResultPage searchFlights(@Valid @RequestBody SearchFlightsRequest searchFlightsRequest) {
        return this.customerService.searchFlightsRequest(searchFlightsRequest);
    }

    @GetMapping("/flights/{id}")
    public Flight searchFlightById(@PathVariable Long id) {
        return this.customerService.searchFlightById(id);
    }

}
