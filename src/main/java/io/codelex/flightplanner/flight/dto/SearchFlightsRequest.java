package io.codelex.flightplanner.flight.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

public class SearchFlightsRequest {

    @Valid
    @NotNull
    private String from;
    @Valid
    @NotNull
    private String to;
    @Valid
    @NotNull
    private LocalDate departureDate;

    public SearchFlightsRequest(String from, String to, String departureDate) {
        this.from = from;
        this.to = to;
        this.departureDate = LocalDate.parse(departureDate);
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    @Override
    public String toString() {
        return "SearchFlightsRequest{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", departureDate='" + departureDate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchFlightsRequest that = (SearchFlightsRequest) o;
        return from.equals(that.from) && to.equals(that.to) && departureDate.equals(that.departureDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, departureDate);
    }
}
