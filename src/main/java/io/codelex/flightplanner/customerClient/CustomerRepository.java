package io.codelex.flightplanner.customerClient;

import io.codelex.flightplanner.customerClient.domain.ResultPage;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepository {

    ResultPage result = new ResultPage();

    public CustomerRepository() {
    }

    public ResultPage getResult() {
        return result;
    }

    public void setResult(ResultPage result) {
        this.result = result;
    }


}
