package io.codelex.flightplanner.flight.dto;

import io.codelex.flightplanner.flight.domain.Flight;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ResultPage {

    int page;
    int totalItems;
    List<Flight> items = new ArrayList<>();

    public ResultPage() {
    }

    public int getPage() {
        return page;
    }

    public void setPage() {
        this.page = 0;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems() {
        this.totalItems = this.items.size();
    }

    public void clearTotalItems() {
        this.totalItems = 0;
    }

    public List<Flight> getItems() {
        return items;
    }

    public void setItems(Flight items) {
        this.items.add(items);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResultPage that = (ResultPage) o;
        return page == that.page && totalItems == that.totalItems && items.equals(that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, totalItems, items);
    }

    @Override
    public String toString() {
        return "ResultPage{" +
                "page=" + page +
                ", totalItems=" + totalItems +
                ", items=" + items +
                '}';
    }
}
