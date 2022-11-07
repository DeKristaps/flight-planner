package io.codelex.flightplanner.testing;

import io.codelex.flightplanner.admin.AdminRepository;
import io.codelex.flightplanner.customerClient.CustomerRepository;
import org.springframework.stereotype.Service;


@Service
public class TestingService {

    private final AdminRepository adminRepository;
    private final CustomerRepository customerRepository;

    public TestingService(AdminRepository adminRepository, CustomerRepository customerRepository) {
        this.adminRepository = adminRepository;
        this.customerRepository = customerRepository;
    }

    public void clear() {
        this.adminRepository.getFlightList().clear();
        this.adminRepository.setId(0);
        this.customerRepository.getResult().getItems().clear();
        this.customerRepository.getResult().clearTotalItems();
    }
}
