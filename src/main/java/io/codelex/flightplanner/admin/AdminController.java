package io.codelex.flightplanner.admin;

import io.codelex.flightplanner.admin.domain.AddFlightRequest;
import io.codelex.flightplanner.admin.domain.Flight;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@Validated
public class AdminController {

    private AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PutMapping("/admin-api/flights")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Flight addFlightRequest(@Valid @RequestBody AddFlightRequest addFlightRequest) {
        return adminService.addFlightRequest(addFlightRequest);
    }

    @DeleteMapping("/admin-api/flights/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public void deleteFlight(@PathVariable Long id) {
        this.adminService.deleteFlight(id);
    }

    @GetMapping("/admin-api/flights/{id}")
    public Flight getFlight(@PathVariable Long id) {
        return this.adminService.findFlight(id);
    }

}
