package customer.service.impl;

import com.alibaba.fastjson2.JSON;
import customer.entity.Contract;
import customer.utils.Utils;
import customer.entity.CustomerOperateRecords;
import customer.dao.CustomerOperateRecordsDao;
import customer.service.CustomerOperateRecordsService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 客户操作记录(CustomerOperateRecords)表服务实现类
 *
 * @author ak.design 337547038
 * @since 2025-07-20 11:20:23
 */
@Service("customerOperateRecordsService")
public class CustomerOperateRecordsServiceImpl implements CustomerOperateRecordsService {
    @Resource
    private CustomerOperateRecordsDao customerOperateRecordsDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public CustomerOperateRecords queryById(Integer id) {
        return this.customerOperateRecordsDao.queryById(id);
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
        CustomerOperateRecords customerOperateRecords = JSON.parseObject(JSON.toJSONString(pages), CustomerOperateRecords.class);//json字符串转java对象
        
        long total = this.customerOperateRecordsDao.count(customerOperateRecords);
        List<Map<String,Object>> list = this.customerOperateRecordsDao.queryAllByLimit(customerOperateRecords,extend);
        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", total);
        return response;
    }

    /**
     * 新增数据
     *
     * @param customerOperateRecords 实例对象
     * @return 实例对象
     */
    @Override
    public CustomerOperateRecords insert(CustomerOperateRecords customerOperateRecords) {
        this.customerOperateRecordsDao.insert(customerOperateRecords);
        return customerOperateRecords;
    }

    /**
     * 修改数据
     *
     * @param customerOperateRecords 实例对象
     * @return 影响的行数
     */
    @Override
    public Integer updateById(CustomerOperateRecords customerOperateRecords) {
        return this.customerOperateRecordsDao.updateById(customerOperateRecords);
        //return this.queryById(customerOperateRecords.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String[] id) {
        return this.customerOperateRecordsDao.deleteById(id) > 0;
    }
}
