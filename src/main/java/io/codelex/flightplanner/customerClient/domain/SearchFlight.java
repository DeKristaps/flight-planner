package io.codelex.flightplanner.customerClient.domain;

import io.codelex.flightplanner.admin.domain.Airport;

import java.time.LocalDate;
import java.util.Objects;

public class SearchFlight {

    Airport from;

    Airport to;

    LocalDate departureDate;

    public SearchFlight() {
    }

    public Airport getFrom() {
        return from;
    }

    public void setFrom(Airport from) {
        this.from = from;
    }

    public Airport getTo() {
        return to;
    }

    public void setTo(Airport to) {
        this.to = to;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchFlight that = (SearchFlight) o;
        return from.equals(that.from) && to.equals(that.to) && departureDate.equals(that.departureDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, departureDate);
    }

    @Override
    public String toString() {
        return "SearchFlight{" +
                "from=" + from +
                ", to=" + to +
                ", departureDate=" + departureDate +
                '}';
    }
}
