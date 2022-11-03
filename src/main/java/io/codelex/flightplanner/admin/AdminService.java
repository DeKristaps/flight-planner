package io.codelex.flightplanner.admin;

import io.codelex.flightplanner.admin.domain.AddFlightRequest;
import io.codelex.flightplanner.admin.domain.Flight;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AdminService {

    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public Flight addFlightRequest(AddFlightRequest addFlightRequest) {
        Flight flight = createFlightFrom(addFlightRequest);
        if (isExistingFlight(flight)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cant add the same flight twice");
        } else if (isSameAirport(flight)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Departure airport and destination airport are the same");
        } else if (!isDatesCorrect(flight)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arrival time is before departure time");
        } else {
            this.adminRepository.addFlight(flight);
            return flight;
        }
    }

    private long getNewFlightId() {
        return this.adminRepository.getId();
    }

    private LocalDateTime dateTimeConverter(String dateTime) {
        return LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER);
    }

    private Flight createFlightFrom(AddFlightRequest addFlightRequest) {
        Flight flight = new Flight();
        flight.setId(getNewFlightId());
        flight.setCarrier(addFlightRequest.getCarrier());
        flight.setFrom(addFlightRequest.getFrom());
        flight.setTo(addFlightRequest.getTo());
        flight.setDepartureTime(dateTimeConverter(addFlightRequest.getDepartureTime()));
        flight.setArrivalTime(dateTimeConverter(addFlightRequest.getArrivalTime()));
        return flight;
    }


    private boolean isExistingFlight(Flight flight) {
        return this.adminRepository.getFlightList().stream().anyMatch(flightsInList ->
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

    public Flight findFlight(Long id) {
        return this.adminRepository.getFlightList().stream().
                filter(flight -> flight.getId() == id).findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public void deleteFlight(Long id) {
        try {
            this.adminRepository.getFlightList().stream().
                    filter(flight -> flight.getId() == id).findFirst().ifPresent(flightToDelete -> this.adminRepository.deleteFlight(flightToDelete));
        } catch (NullPointerException ignored) {
        }
    }
}
