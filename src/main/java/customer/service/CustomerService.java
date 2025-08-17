package customer.service;

import customer.entity.Customer;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
/**
 * (Customer)表服务接口
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:54:02
 */
public interface CustomerService {

    boolean hasPermission(Integer id, String searchType);

    /**
     * 通过ID查询单条数据
     *
     * @param id 参数
     * @return 实例对象
     */
    Customer queryById(Integer id);
    
    /**
     * 分页查询
     * @param pages 筛选条件 分页对象
     * @return 查询结果
     */
    Map<String,Object> queryByPage(Map<String,Object> pages);
    /**
     * 新增数据
     *
     * @param customer 实例对象
     * @return 实例对象
     */
    Customer insert(Customer customer);

    /**
     * 修改数据
     *
     * @param customer 实例对象
     * @return 实例对象
     */
    Integer updateById(Customer customer);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(String[] id);
    boolean importXlsx(MultipartFile file);

    boolean moveCustomerByIds(Map<String, Object> params, String type);

    boolean shareCustomer(Map<String, Object> params);

    String scanCardInput(Map<String, Object> params);

    List<Map<String, Object>> queryNotFollowRecords(Integer userId);

    List<Map<String,Object>> queryContactBirthday(Integer userId);
}
