package customer.service.impl;

import com.alibaba.fastjson2.JSON;
import customer.config.CustomException;
import customer.config.PermissionCheck;
import customer.service.CommonService;
import customer.service.CustomerService;
import customer.service.UserService;
import customer.utils.Utils;
import customer.entity.Contract;
import customer.dao.ContractDao;
import customer.service.ContractService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.*;

/**
 * 合同信息(Contract)表服务实现类
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:53:49
 */
@Service("contractService")
public class ContractServiceImpl implements ContractService {
    @Resource
    private ContractDao contractDao;

    private final UserService userService;

    private final CommonService commonService;

    public ContractServiceImpl(UserService userService, CommonService commonService) {
        this.userService = userService;
        this.commonService = commonService;
    }

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @PermissionCheck(value = {"/contract/contract"})
    @Override
    public Contract queryById(Integer id) {
        Contract contract = contractDao.queryById(id);
        if (Objects.equals(contract.getUserId(), Utils.getCurrentUserId()) || this.userService.isChildrenUser(contract.getUserId())) {
            return contract;
        }
        return null;
    }

    /**
     * 分页查询
     *
     * @param pages 筛选条件分页对象
     * @return 查询结果
     */
    @PermissionCheck(value = {"/contract/contract"})
    @Override
    public Map<String, Object> queryByPage(Map<String, Object> pages) {
        Map<String, Object> extend = this.commonService.queryParams(pages);//处理分页信息
        Contract contract = JSON.parseObject(JSON.toJSONString(pages), Contract.class);//json字符串转java对象
        /*if (contract.getUserId() != null) {
            // 传了查询用户id
            if (!contract.getUserId().equals(Utils.getCurrentUserId())) {
                // 不是查自己的
                boolean isChild = this.userService.isChildrenUser(contract.getUserId());
                if (!isChild) {
                    throw new CustomException("查询异常，请确定有权限查看当前用户");
                }
            }
        } else {
            if ("child".equals(extend.get("search"))) {
                // 查询所有下属
                List<String> ids = this.userService.queryUserChild(Utils.getCurrentUserId(), "");
                extend.put("userIds", ids);
            } else {
                // 没传时只能查看自己的
                contract.setUserId(Utils.getCurrentUserId());
            }
        }*/
        long total = this.contractDao.count(contract, extend);
        List<Map<String, Object>> list = this.contractDao.queryAllByLimit(contract, extend);
        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", total);
        return response;
    }

    /**
     * 新增数据
     *
     * @param contract 实例对象
     * @return 实例对象
     */
    @PermissionCheck(value = {"/contract/contract"})
    @Override
    public Contract insert(Contract contract) {
        contract.setCreatDate(new Date());
        contract.setUserId(Utils.getCurrentUserId());
        contract.setStatus(1);
        this.contractDao.insert(contract);
        return contract;
    }

    /**
     * 修改数据
     *
     * @param contract 实例对象
     * @return 影响的行数
     */
    @PermissionCheck(value = {"/contract/contract"})
    @Override
    public Integer updateById(Contract contract) {
        if (isMeOrChild(contract.getId())) {
            return this.contractDao.updateById(contract);
        } else {
            throw new CustomException("请确认你有权限修改此合同数据");
        }

    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @PermissionCheck(value = {"/contract/contract"})
    @Override
    public boolean deleteById(String[] id) {
        // 使用sql查询判断权限删除
        List<String> ids = this.userService.queryUserChild(Utils.getCurrentUserId(), "");
        Map<String, Object> extend = new HashMap<>();
        extend.put("userIds", ids);
        extend.put("userId", Utils.getCurrentUserId());
        return this.contractDao.deleteById(id, extend) > 0;
    }

    /**
     * 根据合同id判断操作权限，是否为自己或下属的
     *
     * @param contractId 合同id
     * @return 是否为自己或下载的
     */
    @Override
    @Cacheable(value = "contract", key = "#contractId+'_isMeOrChild'")
    public boolean isMeOrChild(Integer contractId) {
        Map<String, Object> extend = new HashMap<>();
        extend.put("contractId", contractId);
        extend.put("userId", Utils.getCurrentUserId());
        List<String> ids = this.userService.queryUserChild(Utils.getCurrentUserId(), "");
        extend.put("userIds", ids);
        return this.contractDao.queryIsMeOrChild(extend) > 0;
    }

    @Override
    public Long total(Contract contract, Map<String, Object> extend) {
        if ("child".equals(extend.get("search")) && extend.get("userIds") == null) {
            extend.put("userIds", this.userService.queryUserChild(Utils.getCurrentUserId(), ""));
        }
        return this.contractDao.count(contract, extend);
    }
}
