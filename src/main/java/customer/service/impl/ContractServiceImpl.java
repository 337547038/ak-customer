package customer.service.impl;

import com.alibaba.fastjson2.JSON;
import customer.utils.Utils;
import customer.entity.Contract;
import customer.dao.ContractDao;
import customer.service.ContractService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Contract queryById(Integer id) {
        return this.contractDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param pages  筛选条件分页对象
     * @return 查询结果
     */
    @Override
    public Map<String, Object> queryByPage(Map<String,Object> pages) {
        Map<String, Object> extend = Utils.getPagination(pages);//处理分页信息
        Contract contract = JSON.parseObject(JSON.toJSONString(pages), Contract.class);//json字符串转java对象
        
        long total = this.contractDao.count(contract);
        List<Map<String,Object>> list = this.contractDao.queryAllByLimit(contract,extend);
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
    @Override
    public Contract insert(Contract contract) {
        this.contractDao.insert(contract);
        return contract;
    }

    /**
     * 修改数据
     *
     * @param contract 实例对象
     * @return 影响的行数
     */
    @Override
    public Integer updateById(Contract contract) {
        return this.contractDao.updateById(contract);
        //return this.queryById(contract.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String[] id) {
        return this.contractDao.deleteById(id) > 0;
    }
}
