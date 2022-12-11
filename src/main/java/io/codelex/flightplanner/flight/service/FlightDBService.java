package io.codelex.flightplanner.flight.service;

import io.codelex.flightplanner.flight.domain.Airport;
import io.codelex.flightplanner.flight.domain.Flight;
import io.codelex.flightplanner.flight.dto.AddFlightRequest;
import io.codelex.flightplanner.flight.dto.ResultPage;
import io.codelex.flightplanner.flight.dto.SearchFlight;
import io.codelex.flightplanner.flight.dto.SearchFlightsRequest;
import io.codelex.flightplanner.flight.repository.AirportRepository;
import io.codelex.flightplanner.flight.repository.FlightRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.ignoreCase;

@Service
@ConditionalOnProperty(prefix = "flightPlanner", name = "appmode", havingValue = "database")
public class FlightDBService implements FlightService {
    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final FlightRepository flightRepository;
    private final AirportRepository airportRepository;

    public FlightDBService(FlightRepository flightRepository, AirportRepository airportRepository) {
        this.flightRepository = flightRepository;
        this.airportRepository = airportRepository;
    }

    @Override
    public synchronized Flight addFlightRequest(AddFlightRequest addFlightRequest) {

        if (addFlightRequest.getFrom().getAirport().trim().equalsIgnoreCase(addFlightRequest.getTo().getAirport().trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Departure airport and destination airport are the same");
        }

        Flight flight = createFlightFrom(addFlightRequest);

        Example<Flight> flightMatcher = Example.of(flight, ExampleMatcher.matching().withIgnorePaths("id").withMatcher("from_airport", ignoreCase()).withMatcher("to_airport", ignoreCase()).withMatcher("carrier", ignoreCase()).withMatcher("departure_time", ignoreCase()).withMatcher("arrival_time", ignoreCase()));

        if (flightRepository.exists(flightMatcher)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cant add the same flight twice");
        } else if (!isDatesCorrect(flight)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arrival time is before departure time");
        } else {
            this.flightRepository.save(flight);
            return flight;
        }
    }

    @Override
    public Flight createFlightFrom(AddFlightRequest addFlightRequest) {
        Flight flight = new Flight();
        Airport from = getOrCreate(addFlightRequest.getFrom());
        Airport to = getOrCreate(addFlightRequest.getTo());
        flight.setCarrier(addFlightRequest.getCarrier());
        flight.setFrom(from);
        flight.setTo(to);
        flight.setDepartureTime(dateTimeConverter(addFlightRequest.getDepartureTime()));
        flight.setArrivalTime(dateTimeConverter(addFlightRequest.getArrivalTime()));
        return flight;
    }

    private Airport getOrCreate(Airport airport) {
        Optional<Airport> optionalAirport = airportRepository.findById(airport.getAirport());
        return optionalAirport.orElseGet(() -> airportRepository.save(airport));
    }

    private LocalDateTime dateTimeConverter(String dateTime) {
        return LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER);
    }

    private boolean isDatesCorrect(Flight flight) {
        return flight.getDepartureTime().isBefore(flight.getArrivalTime());
    }

    @Override
    public void deleteFlight(Long id) {
        if (this.flightRepository.existsById(id)) {
            this.flightRepository.deleteById(id);
        }
    }

    @Override
    public void clear() {
        flightRepository.deleteAll();
        airportRepository.deleteAll();
    }

    @Override
    public Flight findFlight(Long id) {
        return this.flightRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public List<Airport> getAirport(String searchParam) {
        String search = searchParam.replaceAll("[^a-zA-Z\\d\\s:]", "").toUpperCase().trim();

        return this.airportRepository.getAirportQuery(search);
    }

    @Override
    public ResultPage searchFlightsRequest(SearchFlightsRequest searchFlightsRequest) {
        ResultPage resultPage = new ResultPage();
        SearchFlight searchFlight = createSearchFlight(searchFlightsRequest);
        if (searchFlightsRequest.getFrom().equalsIgnoreCase(searchFlightsRequest.getTo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Departure airport and destination airport are the same");
        }

        List<Flight> flightList = this.flightRepository.getFlightQuery(
                searchFlight.getFrom(),
                searchFlight.getTo(),
                searchFlight.getDepartureDate());

        flightList.forEach(resultPage::setItems);
        resultPage.setTotalItems();
        resultPage.setPage();


        return resultPage;
    }

    @Override
    public SearchFlight createSearchFlight(SearchFlightsRequest searchFlightsRequest) {
        SearchFlight searchFlight = new SearchFlight();
        Airport formAirport = this.airportRepository.getAirportQuery(searchFlightsRequest.getFrom())
                .stream().filter(airport -> airport.getAirport().equals(searchFlightsRequest.getFrom()))
                .findFirst().orElse(null);
        Airport toAirport = this.airportRepository.getAirportQuery(searchFlightsRequest.getTo())
                .stream().filter(airport -> airport.getAirport().equals(searchFlightsRequest.getTo()))
                .findFirst().orElse(null);

        if (formAirport != null && toAirport != null) {
            searchFlight.setFrom(formAirport);
            searchFlight.setTo(toAirport);
            searchFlight.setDepartureDate(searchFlightsRequest.getDepartureDate());
        }
        return searchFlight;
    }
}
