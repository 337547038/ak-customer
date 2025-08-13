package customer.controller;

import customer.entity.Customer;
import customer.service.AnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    @Resource
    private AnalysisService analysisService;

    @Operation(summary = "客户数据分析")
    @Parameters({
            @Parameter(name = "userId", description = "查询指定用户的客户")
    })
    @PostMapping("customer")
    public Map<String, Object> analysisCustomers(@RequestBody Map<String, Object> customers) {
        return analysisService.analysisCustomer(customers);
    }

    /**
     * 统计员工客户数量及合同
     *
     * @return list
     */
    @Operation(summary = "员工客户数量统计")
    @PostMapping("customerNum")
    public List<Map<String, Object>> userCustomerNum() {
        return analysisService.userCustomerNum();
    }

    @Operation(summary = "跟进分析")
    @PostMapping("follow")
    public List<Map<String, Object>> customerFollow() {
        return analysisService.customerFollow();
    }

    @Operation(summary = "合同排行分析")
    @PostMapping("contract")
    public List<Map<String, Object>> customerContract() {
        return analysisService.customerContract();
    }

    /**
     * 首页汇总
     *
     * @return result
     */
    @Operation(summary = "首页汇总")
    @PostMapping("summary")
    public Map<String, Object> summary() {
        return analysisService.summary();
    }
}
