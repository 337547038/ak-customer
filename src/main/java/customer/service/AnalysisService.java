package customer.service;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface AnalysisService {
    Map<String, Object> analysisCustomer(@RequestBody Map<String, Object> customers);

    List<Map<String, Object>> userCustomerNum();
    List<Map<String, Object>> customerFollow();

    List<Map<String, Object>> customerContract();

    Map<String, Object> summary();
}
