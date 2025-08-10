package customer.service.impl;

import customer.config.PermissionCheck;
import customer.service.AnalysisService;
import customer.service.CustomerService;

import customer.service.UserService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service("analysisService")
public class analysisServiceImpl implements AnalysisService {

    private final CustomerService customerService;
    private final UserService userService;

    public analysisServiceImpl(CustomerService customerService, UserService userService) {
        this.customerService = customerService;
        this.userService = userService;
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
}
