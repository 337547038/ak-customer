package customer.service;

import customer.entity.User;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;
/**
 * (User)表服务接口
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:55:00
 */
public interface UserService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    User queryById(Integer id);
    
    /**
     * 分页查询
     * @param pages 筛选条件 分页对象
     * @return 查询结果
     */
    Map<String,Object> queryByPage(Map<String,Object> pages);
    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    User insert(User user);

    /**
     * 修改数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    Integer updateById(User user);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(String[] id);

    User login(User user, String ipAddress);


    User scanLogin(Map<String, Object> query, String ipAddress);

    Boolean bindWX(Map<String, Object> params);

    List<Map<String, Object>> queryByIds(String ids);

    boolean tokenVerify(Integer userId);

    String queryUserFather(Integer userId);

    /**
     * 根据用户id返回所有子级
     * @param userId 用户id
     * @return 子级id数组
     */
    List<Map<String, Object>> queryUserChild(Integer userId);

    List<String> queryUserChild(Integer userId, String type);

    boolean isChildrenUser(Integer userId);

    boolean hasChild();

    List<Map<String, Object>> userCustomer();


    List<Map<String,Object>> queryUserFollow();

    @Cacheable(value = "analysis", key = "T(customer.utils.Utils).getCurrentUserId()+'_contract'")
    List<Map<String, Object>> queryUserContract();
}
