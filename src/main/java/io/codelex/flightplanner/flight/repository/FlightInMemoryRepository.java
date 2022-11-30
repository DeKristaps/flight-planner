package io.codelex.flightplanner.flight.repository;

import io.codelex.flightplanner.flight.domain.Airport;
import io.codelex.flightplanner.flight.domain.Flight;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@ConditionalOnProperty(prefix = "flightPlanner", name = "appmode", havingValue = "inmemory")
public class FlightInMemoryRepository {

    private AtomicInteger id = new AtomicInteger(0);

    private List<Flight> flightList = new ArrayList<>();

    private final List<Airport> airports = new ArrayList<>();

    public FlightInMemoryRepository() {
    }

    public void addFlight(Flight flight) {
        this.flightList.add(flight);
    }

    public void addAirport(Airport airport) {
        this.airports.add(airport);
    }

    public List<Airport> getAirports() {
        return this.airports;
    }

    public void deleteFlight(Flight flight) {
        this.flightList.remove(flight);
    }

    public int getId() {
        return id.getAndIncrement();
    }

    public void setId(long id) {
        this.id = new AtomicInteger(0);
    }

    public List<Flight> getFlightList() {
        return flightList;
    }

    public void setFlightList(List<Flight> flightList) {
        this.flightList = flightList;
    }
}




