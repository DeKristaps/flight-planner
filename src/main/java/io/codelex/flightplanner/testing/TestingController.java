package io.codelex.flightplanner.testing;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestingController {

    private TestingService testingService;

    public TestingController(TestingService testingService) {
        this.testingService = testingService;
    }

    @PostMapping("/testing-api/clear")
    public void clear() {
        this.testingService.clear();
    }
}
