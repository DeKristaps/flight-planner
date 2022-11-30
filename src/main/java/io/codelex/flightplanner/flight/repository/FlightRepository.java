package io.codelex.flightplanner.flight.repository;

import io.codelex.flightplanner.flight.domain.Flight;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(prefix = "flightPlanner", name = "appmode", havingValue = "database")
public interface FlightRepository extends JpaRepository<Flight, Long> {
}
