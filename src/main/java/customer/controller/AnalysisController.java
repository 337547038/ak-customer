package customer.controller;

import customer.entity.Customer;
import customer.service.AnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Map<String, Object>> analysisCustomers(@RequestBody Map<String, Object> customers) {
        return ResponseEntity.ok(analysisService.analysisCustomer(customers));
    }

    /**
     * 统计员工客户数量及合同
     *
     * @return list
     */
    @Operation(summary = "员工客户数量统计")
    @PostMapping("customerNum")
    public ResponseEntity<List<Map<String, Object>>> userCustomerNum() {
        return ResponseEntity.ok(analysisService.userCustomerNum());
    }

    @Operation(summary = "跟进分析")
    @PostMapping("follow")
    public ResponseEntity<List<Map<String, Object>>> customerFollow() {
        return ResponseEntity.ok(analysisService.customerFollow());
    }

    @Operation(summary = "合同排行分析")
    @PostMapping("contract")
    public ResponseEntity<List<Map<String, Object>>> customerContract() {
        return ResponseEntity.ok(analysisService.customerContract());
    }

    /**
     * 首页汇总
     *
     * @return result
     */
    @Operation(summary = "首页汇总")
    @PostMapping("summary")
    public ResponseEntity<Map<String, Object>> summary() {
        return ResponseEntity.ok(analysisService.summary());
    }
}
