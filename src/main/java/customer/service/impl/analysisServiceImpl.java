package customer.service.impl;

import customer.config.PermissionCheck;
import customer.entity.Contract;
import customer.entity.ContractPayment;
import customer.service.*;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service("analysisService")
public class analysisServiceImpl implements AnalysisService {

    private final CustomerService customerService;
    private final UserService userService;
    private final ContractService contractService;
    private final ContractPaymentService contractPaymentService;
    private final ContactService contactService;

    public analysisServiceImpl(CustomerService customerService, UserService userService, ContractService contractService, ContractPaymentService contractPaymentService, ContactService contactService) {
        this.customerService = customerService;
        this.userService = userService;
        this.contractService = contractService;
        this.contractPaymentService = contractPaymentService;
        this.contactService = contactService;
    }

    @Cacheable(value = "analysis", key = "T(customer.utils.Utils).getCurrentUserId()+'_'+ #params.get('userId')+ '_Customer'")
    @PermissionCheck(value = {"/analysis/type", "/analysis/source", "/analysis/industry", "/analysis/area"})
    @Override
    public Map<String, Object> analysisCustomer(@RequestBody Map<String, Object> params) {
        Map<String, Object> query = new HashMap<>();
        Map<String, Object> extend = new HashMap<>();
        query.put("userId", params.get("userId"));
        extend.put("pageSize", 0);
        extend.put("columns", "diy");
        extend.put("diyColumns", List.of("type", "source", "industry", "id", "area"));
        query.put("extend", extend);
        return this.customerService.queryByPage(query);
    }

    /**
     * 员工客户量分析
     *
     * @return list
     */
    @PermissionCheck(value = {"/analysis/customer"})
    @Override
    public List<Map<String, Object>> userCustomerNum() {
        return this.userService.userCustomer();
    }

    /**
     * 跟进分析
     *
     * @return list
     */
    @PermissionCheck(value = {"/analysis/follow"})
    @Override
    public List<Map<String, Object>> customerFollow() {
        return this.userService.queryUserFollow();
    }

    @PermissionCheck(value = {"/analysis/contract"})
    @Override
    public List<Map<String, Object>> customerContract() {
        return this.userService.queryUserContract();
    }

    @Override
    public Map<String, Object> summary() {
        Map<String, Object> extend = new HashMap<>();
        extend.put("search", "child");
        Contract contract = new Contract();
        contract.setStatus(1);
        long todoContract = this.contractService.total(contract, extend);
        Map<String, Object> result = new HashMap<>();
        result.put("contract", todoContract); // 待审核合同
        ContractPayment contractPayment = new ContractPayment();
        contractPayment.setStatus(1);
        long todoPayment = this.contractPaymentService.total(contractPayment, extend);
        result.put("payment", todoPayment);
        // 待跟进客户列表
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> listExtend = new HashMap<>();

        params.put("extend", listExtend);
        Map<String, Object> list = this.contactService.queryByPage(params);
        return result;
    }
}
