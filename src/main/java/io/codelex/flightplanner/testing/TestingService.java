package io.codelex.flightplanner.testing;

import io.codelex.flightplanner.admin.AdminRepository;
import org.springframework.stereotype.Service;


@Service
public class TestingService {

    private AdminRepository adminRepository;

    public TestingService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public void clear() {
        this.adminRepository.getFlightList().clear();
        this.adminRepository.setId(0);
    }
}
