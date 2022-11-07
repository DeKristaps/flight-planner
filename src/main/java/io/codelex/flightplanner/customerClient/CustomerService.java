package io.codelex.flightplanner.customerClient;

import io.codelex.flightplanner.admin.AdminRepository;
import io.codelex.flightplanner.admin.AdminService;
import io.codelex.flightplanner.admin.domain.Airport;
import io.codelex.flightplanner.admin.domain.Flight;
import io.codelex.flightplanner.customerClient.domain.ResultPage;
import io.codelex.flightplanner.customerClient.domain.SearchFlight;
import io.codelex.flightplanner.customerClient.domain.SearchFlightsRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CustomerService {

    private final AdminRepository adminRepository;
    private final CustomerRepository customerRepository;
    private final AdminService adminService;

    public CustomerService(AdminRepository adminRepository, CustomerRepository customerRepository, AdminService adminService) {
        this.adminRepository = adminRepository;
        this.customerRepository = customerRepository;
        this.adminService = adminService;
    }

    public List<Airport> getAirport(String searchParam) {
        String formattedSearchParam = formatSearchParam(searchParam);
        List<Airport> airports = this.adminRepository.getFlightList().stream().map(Flight::getFrom).toList();

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
        this.adminRepository.getFlightList().forEach(flight -> {
            if (flight.getFrom().equals(searchFlight.getFrom()) &&
                    flight.getTo().equals(searchFlight.getTo()) &&
                    flight.getDepartureTime().isAfter(searchFlight.getDepartureDate().atStartOfDay())) {
                resultPage.setItems(flight);
            }
        });
        resultPage.setTotalItems();
        resultPage.setPage();

        this.customerRepository.setResult(resultPage);
        return this.customerRepository.getResult();
    }


    public Flight searchFlightById(Long id) {
        return this.adminService.findFlight(id);
    }

    private SearchFlight createSearchFlight(SearchFlightsRequest searchFlightsRequest) {
        SearchFlight searchFlight = new SearchFlight();
        Airport formAirport = this.adminRepository.getAirports().stream()
                .filter(airport -> airport.getAirport().equals(searchFlightsRequest.getFrom())).findFirst().orElse(null);
        Airport toAirport = this.adminRepository.getAirports().stream()
                .filter(airport -> airport.getAirport().equals(searchFlightsRequest.getTo())).findFirst().orElse(null);

        if (formAirport != null && toAirport != null) {
            searchFlight.setFrom(formAirport);
            searchFlight.setTo(toAirport);
            searchFlight.setDepartureDate(searchFlightsRequest.getDepartureDate());
        }
        return searchFlight;
    }

}
