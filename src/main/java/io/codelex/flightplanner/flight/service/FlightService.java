package io.codelex.flightplanner.flight.service;

import io.codelex.flightplanner.flight.domain.Airport;
import io.codelex.flightplanner.flight.domain.Flight;
import io.codelex.flightplanner.flight.dto.AddFlightRequest;
import io.codelex.flightplanner.flight.dto.ResultPage;
import io.codelex.flightplanner.flight.dto.SearchFlight;
import io.codelex.flightplanner.flight.dto.SearchFlightsRequest;

import java.util.List;

public interface FlightService {

    Flight addFlightRequest(AddFlightRequest addFlightRequest);

    Flight createFlightFrom(AddFlightRequest addFlightRequest);

    void deleteFlight(Long id);

    void clear();

    Flight findFlight(Long id);

    List<Airport> getAirport(String searchParam);

    ResultPage searchFlightsRequest(SearchFlightsRequest searchFlightsRequest);

    SearchFlight createSearchFlight(SearchFlightsRequest searchFlightsRequest);
}
