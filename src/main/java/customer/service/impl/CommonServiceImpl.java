package customer.service.impl;

import customer.config.CustomException;
import customer.service.CommonService;
import customer.service.CustomerService;
import customer.service.UserService;
import customer.utils.Utils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service("common")
public class CommonServiceImpl implements CommonService {

    private final UserService userService;
    private final CustomerService customerService;

    public CommonServiceImpl(UserService userService, CustomerService customerService) {
        this.userService = userService;
        this.customerService = customerService;
    }

    @Override
    public Map<String, Object> queryParams(Map<String, Object> params) {
        Map<String, Object> extend = Utils.getPagination(params);//处理分页信息
        Object userId = params.get("userId");
        Object typeStr = extend.get("search");
        Object specialCustomerId = params.get("specialCustomer");
        if (Objects.equals(typeStr, "comInvalidShare") && specialCustomerId != null) {
            // 公海无效分享权限判断
            this.customerService.hasPermission((Integer) specialCustomerId, "detail");
            //extend.put("specialCustomerId", specialCustomerId);
        } else {
            if (userId == null || userId == "") {
                if (Objects.equals(typeStr, "child")) {
                    // 查看所有下属
                    List<String> ids = this.userService.queryUserChild(Utils.getCurrentUserId(), "");
                    if (ids == null || ids.isEmpty()) {
                        throw new CustomException("没有找到下属成员");
                    }
                    extend.put("userIds", ids);
                } else {
                    // 查看自己的
                    extend.put("userId", Utils.getCurrentUserId());
                }
            } else if (Objects.equals(userId, Utils.getCurrentUserId())) {
                //　查自己的
                extend.put("userId", Utils.getCurrentUserId());
            } else {
                // 查指定下属的
                boolean isChildUser = this.userService.isChildrenUser((Integer) userId);
                if (!isChildUser) {
                    throw new CustomException("请确认你是否具有查看此用户权限");
                }
                extend.put("userId", userId);
            }
        }
        return extend;
    }

}
