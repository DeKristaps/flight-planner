package io.codelex.flightplanner.flight;

import io.codelex.flightplanner.flight.domain.Airport;
import io.codelex.flightplanner.flight.domain.Flight;
import io.codelex.flightplanner.flight.dto.AddFlightRequest;
import io.codelex.flightplanner.flight.dto.ResultPage;
import io.codelex.flightplanner.flight.dto.SearchFlight;
import io.codelex.flightplanner.flight.dto.SearchFlightsRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FlightService {

    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public synchronized Flight addFlightRequest(AddFlightRequest addFlightRequest) {
        Flight flight = createFlightFrom(addFlightRequest);
        if (isExistingFlight(flight)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cant add the same flight twice");
        } else if (isSameAirport(flight)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Departure airport and destination airport are the same");
        } else if (!isDatesCorrect(flight)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arrival time is before departure time");
        } else {
            this.flightRepository.addFlight(flight);
            if (!flightRepository.getAirports().contains(addFlightRequest.getFrom())) {
                this.flightRepository.addAirport(addFlightRequest.getFrom());
            }
            if (!flightRepository.getAirports().contains(addFlightRequest.getTo())) {
                this.flightRepository.addAirport(addFlightRequest.getTo());
            }
            return flight;
        }
    }

    private LocalDateTime dateTimeConverter(String dateTime) {
        return LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER);
    }

    private synchronized Flight createFlightFrom(AddFlightRequest addFlightRequest) {
        Flight flight = new Flight();
        flight.setId(this.flightRepository.getId());
        flight.setCarrier(addFlightRequest.getCarrier());
        flight.setFrom(addFlightRequest.getFrom());
        flight.setTo(addFlightRequest.getTo());
        flight.setDepartureTime(dateTimeConverter(addFlightRequest.getDepartureTime()));
        flight.setArrivalTime(dateTimeConverter(addFlightRequest.getArrivalTime()));
        return flight;
    }


    private boolean isExistingFlight(Flight flight) {
        return this.flightRepository.getFlightList().stream().anyMatch(flightsInList ->
                flightsInList.getFrom().equals(flight.getFrom()) &&
                        flightsInList.getTo().equals(flight.getTo()) &&
                        flightsInList.getCarrier().equals(flight.getCarrier()) &&
                        flightsInList.getArrivalTime().equals(flight.getArrivalTime()) &&
                        flightsInList.getDepartureTime().equals(flight.getDepartureTime()));
    }

    private boolean isSameAirport(Flight flight) {
        return flight.getFrom().toString().equalsIgnoreCase(flight.getTo().toString());
    }

    private boolean isDatesCorrect(Flight flight) {
        return flight.getDepartureTime().isBefore(flight.getArrivalTime());
    }

    public synchronized Flight findFlight(Long id) {
        return this.flightRepository.getFlightList().stream().
                filter(flight -> flight.getId() == id).findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public synchronized void deleteFlight(Long id) {
        this.flightRepository.getFlightList().removeIf(flight -> flight.getId() == id);
    }

    public void clear() {
        this.flightRepository.getFlightList().clear();
        this.flightRepository.setId(0);
    }

    public List<Airport> getAirport(String searchParam) {
        String formattedSearchParam = formatSearchParam(searchParam);
        List<Airport> airports = this.flightRepository.getFlightList().stream().map(Flight::getFrom).toList();

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
        this.flightRepository.getFlightList().forEach(flight -> {
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

    private SearchFlight createSearchFlight(SearchFlightsRequest searchFlightsRequest) {
        SearchFlight searchFlight = new SearchFlight();
        Airport formAirport = this.flightRepository.getAirports().stream()
                .filter(airport -> airport.getAirport().equals(searchFlightsRequest.getFrom())).findFirst().orElse(null);
        Airport toAirport = this.flightRepository.getAirports().stream()
                .filter(airport -> airport.getAirport().equals(searchFlightsRequest.getTo())).findFirst().orElse(null);

        if (formAirport != null && toAirport != null) {
            searchFlight.setFrom(formAirport);
            searchFlight.setTo(toAirport);
            searchFlight.setDepartureDate(searchFlightsRequest.getDepartureDate());
        }
        return searchFlight;
    }
}
