package customer.dao;

import customer.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * (Customer)表数据库访问层
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:54:02
 */
 @Mapper
public interface CustomerDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Customer queryById(Integer id);

    /**
     * 查询指定行数据
     *@param customer 筛选条件
     *@param extend 扩展参数
     * @return 对象列表
     */
    List<Map<String,Object>> queryAllByLimit(@Param("query") Customer customer,@Param("extend") Map<String,Object> extend);

    /**
     * 统计总行数
     *
     * @param customer 查询条件
     * @return 总行数
     */
    long count(Customer customer);
    long exist(Customer customer);

    /**
     * 新增数据
     *
     * @param customer 实例对象
     * @return 影响行数
     */
    int insert(Customer customer);

    /**
     * 修改数据
     *
     * @param customer 实例对象
     * @return 影响行数
     */
    int updateById(Customer customer);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("id") String[] id);

}

