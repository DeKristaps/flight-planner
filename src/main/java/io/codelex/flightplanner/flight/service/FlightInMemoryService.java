package io.codelex.flightplanner.flight.service;

import io.codelex.flightplanner.flight.domain.Airport;
import io.codelex.flightplanner.flight.domain.Flight;
import io.codelex.flightplanner.flight.dto.AddFlightRequest;
import io.codelex.flightplanner.flight.dto.ResultPage;
import io.codelex.flightplanner.flight.dto.SearchFlight;
import io.codelex.flightplanner.flight.dto.SearchFlightsRequest;
import io.codelex.flightplanner.flight.repository.FlightInMemoryRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@ConditionalOnProperty(prefix = "flightPlanner", name = "appmode", havingValue = "inmemory")
public class FlightInMemoryService implements FlightService {

    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final FlightInMemoryRepository flightInMemoryRepository;

    public FlightInMemoryService(FlightInMemoryRepository flightInMemoryRepository) {
        this.flightInMemoryRepository = flightInMemoryRepository;
    }

    public synchronized Flight addFlightRequest(AddFlightRequest addFlightRequest) {

        if (addFlightRequest.getFrom().getAirport().trim().equalsIgnoreCase(addFlightRequest.getTo().getAirport().trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Departure airport and destination airport are the same");
        }

        Flight flight = createFlightFrom(addFlightRequest);
        if (isExistingFlight(flight)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cant add the same flight twice");
        } else if (!isDatesCorrect(flight)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arrival time is before departure time");
        } else {
            this.flightInMemoryRepository.addFlight(flight);
            if (!flightInMemoryRepository.getAirports().contains(addFlightRequest.getFrom())) {
                this.flightInMemoryRepository.addAirport(addFlightRequest.getFrom());
            }
            if (!flightInMemoryRepository.getAirports().contains(addFlightRequest.getTo())) {
                this.flightInMemoryRepository.addAirport(addFlightRequest.getTo());
            }
            return flight;
        }
    }

    private LocalDateTime dateTimeConverter(String dateTime) {
        return LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER);
    }

    public synchronized Flight createFlightFrom(AddFlightRequest addFlightRequest) {
        Flight flight = new Flight();
        flight.setId(this.flightInMemoryRepository.getId());
        flight.setCarrier(addFlightRequest.getCarrier());
        flight.setFrom(addFlightRequest.getFrom());
        flight.setTo(addFlightRequest.getTo());
        flight.setDepartureTime(dateTimeConverter(addFlightRequest.getDepartureTime()));
        flight.setArrivalTime(dateTimeConverter(addFlightRequest.getArrivalTime()));
        return flight;
    }


    private boolean isExistingFlight(Flight flight) {
        return this.flightInMemoryRepository.getFlightList().stream().anyMatch(flightsInList ->
                flightsInList.getFrom().equals(flight.getFrom()) &&
                        flightsInList.getTo().equals(flight.getTo()) &&
                        flightsInList.getCarrier().equals(flight.getCarrier()) &&
                        flightsInList.getArrivalTime().equals(flight.getArrivalTime()) &&
                        flightsInList.getDepartureTime().equals(flight.getDepartureTime()));
    }

    private boolean isDatesCorrect(Flight flight) {
        return flight.getDepartureTime().isBefore(flight.getArrivalTime());
    }

    public synchronized Flight findFlight(Long id) {
        return this.flightInMemoryRepository.getFlightList().stream().
                filter(flight -> flight.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public synchronized void deleteFlight(Long id) {
        this.flightInMemoryRepository.getFlightList().removeIf(flight -> flight.getId() == id);
    }

    public void clear() {
        this.flightInMemoryRepository.getFlightList().clear();
        this.flightInMemoryRepository.setId(0);
    }

    public List<Airport> getAirport(String searchParam) {
        String formattedSearchParam = formatSearchParam(searchParam);
        List<Airport> airports = this.flightInMemoryRepository.getFlightList().stream().map(Flight::getFrom).toList();

        return airports.stream().filter(airport ->
                        airport.getAirport().toUpperCase().startsWith(formattedSearchParam) ||
                                airport.getCity().toUpperCase().startsWith(formattedSearchParam) ||
                                airport.getCountry().toUpperCase().startsWith(formattedSearchParam))
                .findAny().stream().toList();
    }

    private String formatSearchParam(String searchParam) {
        return searchParam.replaceAll("[^a-zA-Z\\d\\s:]", "").toUpperCase().trim();
    }

    public ResultPage searchFlightsRequest(SearchFlightsRequest searchFlightsRequest) {
        ResultPage resultPage = new ResultPage();
        SearchFlight searchFlight = createSearchFlight(searchFlightsRequest);
        if (searchFlightsRequest.getFrom().equalsIgnoreCase(searchFlightsRequest.getTo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Departure airport and destination airport are the same");
        }
        this.flightInMemoryRepository.getFlightList().forEach(flight -> {
            if (flight.getFrom().equals(searchFlight.getFrom()) &&
                    flight.getTo().equals(searchFlight.getTo()) &&
                    flight.getDepartureTime().isAfter(searchFlight.getDepartureDate().atStartOfDay())) {
                resultPage.setItems(flight);
            }
        });
        resultPage.setTotalItems();
        resultPage.setPage();


        return resultPage;
    }

    public SearchFlight createSearchFlight(SearchFlightsRequest searchFlightsRequest) {
        SearchFlight searchFlight = new SearchFlight();
        Airport formAirport = this.flightInMemoryRepository.getAirports().stream()
                .filter(airport -> airport.getAirport().equals(searchFlightsRequest.getFrom())).findFirst().orElse(null);
        Airport toAirport = this.flightInMemoryRepository.getAirports().stream()
                .filter(airport -> airport.getAirport().equals(searchFlightsRequest.getTo())).findFirst().orElse(null);

        if (formAirport != null && toAirport != null) {
            searchFlight.setFrom(formAirport);
            searchFlight.setTo(toAirport);
            searchFlight.setDepartureDate(searchFlightsRequest.getDepartureDate());
        }
        return searchFlight;
    }
}
