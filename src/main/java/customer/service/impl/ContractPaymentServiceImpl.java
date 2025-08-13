package customer.service.impl;

import com.alibaba.fastjson2.JSON;
import customer.config.CustomException;
import customer.config.PermissionCheck;
import customer.service.ContractService;
import customer.service.UserService;
import customer.utils.Utils;
import customer.entity.ContractPayment;
import customer.dao.ContractPaymentDao;
import customer.service.ContractPaymentService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 合同回款(ContractPayment)表服务实现类
 *
 * @author ak.design 337547038
 * @since 2025-08-09 12:55:23
 */
@Service("contractPaymentService")
public class ContractPaymentServiceImpl implements ContractPaymentService {
    @Resource
    private ContractPaymentDao contractPaymentDao;
    private final UserService userService;
    private final ContractService contractService;

    public ContractPaymentServiceImpl(UserService userService, ContractService contractService) {
        this.userService = userService;
        this.contractService = contractService;
    }

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public ContractPayment queryById(Integer id) {
        ContractPayment contractPayment = contractPaymentDao.queryById(id);
        if (this.contractService.isMeOrChild(contractPayment.getContractId())) {
            return contractPayment;
        } else {
            throw new CustomException("请确认你是否具有查看此用户的合同信息权限");
        }
    }

    /**
     * 分页查询
     *
     * @param pages 筛选条件分页对象
     * @return 查询结果
     */
    @Override
    @PermissionCheck(value = {"/contract/payment"})
    public Map<String, Object> queryByPage(Map<String, Object> pages) {
        Map<String, Object> extend = Utils.getPagination(pages);//处理分页信息
        ContractPayment contractPayment = JSON.parseObject(JSON.toJSONString(pages), ContractPayment.class);//json字符串转java对象
        Object userId = pages.get("userId");
        if (userId == null) {
            if (extend.get("search") == "child") {
                // 查询所有下属
                List<String> ids = this.userService.queryUserChild(Utils.getCurrentUserId(), "");
                extend.put("userIds", ids);
            } else {
                // 查看自己的
                extend.put("userId", Utils.getCurrentUserId());
            }
        } else if (userId == Utils.getCurrentUserId()) {
            extend.put("userId", Utils.getCurrentUserId());
        } else {
            // 查看指定下属的
            boolean isChildUser = this.userService.isChildrenUser((Integer) userId);
            if (!isChildUser) {
                throw new CustomException("请确认你是否具有查看此用户的合同信息权限");
            }
            extend.put("userId", userId);
        }
        long total = this.contractPaymentDao.count(contractPayment, extend);
        List<Map<String, Object>> list = this.contractPaymentDao.queryAllByLimit(contractPayment, extend);
        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", total);
        return response;
    }

    /**
     * 新增数据
     *
     * @param contractPayment 实例对象
     * @return 实例对象
     */
    @Override
    @PermissionCheck(value = {"/contract/payment"})
    public ContractPayment insert(ContractPayment contractPayment) {
        contractPayment.setCreatDate(new Date());
        contractPayment.setStatus(0);
        this.contractPaymentDao.insert(contractPayment);
        return contractPayment;
    }

    /**
     * 修改数据
     *
     * @param contractPayment 实例对象
     * @return 影响的行数
     */
    @Override
    public Integer updateById(ContractPayment contractPayment) {
        if (this.contractService.isMeOrChild(contractPayment.getContractId())) {
            return this.contractPaymentDao.updateById(contractPayment);
        } else {
            throw new CustomException("请确认你有操作此记录的权限");
        }
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String[] id) {
        List<String> ids = this.userService.queryUserChild(Utils.getCurrentUserId(), "");
        Map<String, Object> extend = new HashMap<>();
        extend.put("userIds", ids);
        extend.put("userId", Utils.getCurrentUserId());
        return this.contractPaymentDao.deleteById(id, extend) > 0;
    }

    @Override
    public Long total(ContractPayment contractPayment, Map<String, Object> extend) {
        return this.contractPaymentDao.count(contractPayment, extend);
    }
}
