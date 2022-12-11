package io.codelex.flightplanner.flight.repository;

import io.codelex.flightplanner.flight.domain.Airport;
import io.codelex.flightplanner.flight.domain.Flight;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@ConditionalOnProperty(prefix = "flightPlanner", name = "appmode", havingValue = "database")
public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("SELECT f FROM flight f WHERE f.from = ?1 " +
            "AND f.to = ?2 " +
            "AND f.departureTime > cast(?3 as timestamp)")
    List<Flight> getFlightQuery(Airport from, Airport to, LocalDate departureDate);
}
