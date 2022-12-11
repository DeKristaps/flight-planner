package io.codelex.flightplanner.flight.repository;

import io.codelex.flightplanner.flight.domain.Airport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@ConditionalOnProperty(prefix = "flightPlanner", name = "appmode", havingValue = "database")
public interface AirportRepository extends JpaRepository<Airport, String> {

    @Query("SELECT a FROM airport a WHERE upper(a.airport) like ?1% OR upper(a.city) like ?1% OR upper(a.country) like ?1%")
    List<Airport> getAirportQuery(String search);
}
