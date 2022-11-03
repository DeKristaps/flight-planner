package io.codelex.flightplanner.admin;

import io.codelex.flightplanner.admin.domain.Flight;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class AdminRepository {

    private AtomicInteger id = new AtomicInteger(0);

    private List<Flight> flightList = new ArrayList<>();

    public AdminRepository() {
    }

    public void addFlight(Flight flight) {
        this.flightList.add(flight);
    }

    public void deleteFlight(Flight flight) {
        this.flightList.remove(flight);
    }


    public int getId() {
        return id.getAndIncrement();
    }

    public List<Flight> getFlightList() {
        return flightList;
    }

    public void setId(long id) {
        this.id = new AtomicInteger(0);
    }

    public void setFlightList(List<Flight> flightList) {
        this.flightList = flightList;
    }
}
