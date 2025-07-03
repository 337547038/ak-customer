package customer.service.impl;

import com.alibaba.fastjson2.JSON;
import customer.utils.Utils;
import customer.entity.Customer;
import customer.dao.CustomerDao;
import customer.service.CustomerService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * (Customer)表服务实现类
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:54:02
 */
@Service("customerService")
public class CustomerServiceImpl implements CustomerService {
    @Resource
    private CustomerDao customerDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Customer queryById(Integer id) {
        return this.customerDao.queryById(id);
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
        Customer customer = JSON.parseObject(JSON.toJSONString(pages), Customer.class);//json字符串转java对象
        
        long total = this.customerDao.count(customer);
        List<Map<String,Object>> list = this.customerDao.queryAllByLimit(customer,extend);
        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", total);
        return response;
    }

    /**
     * 新增数据
     *
     * @param customer 实例对象
     * @return 实例对象
     */
    @Override
    public Customer insert(Customer customer) {
        this.customerDao.insert(customer);
        return customer;
    }

    /**
     * 修改数据
     *
     * @param customer 实例对象
     * @return 影响的行数
     */
    @Override
    public Integer updateById(Customer customer) {
        return this.customerDao.updateById(customer);
        //return this.queryById(customer.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String[] id) {
        return this.customerDao.deleteById(id) > 0;
    }
}
