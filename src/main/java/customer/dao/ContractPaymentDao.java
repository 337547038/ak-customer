package customer.dao;

import customer.entity.ContractPayment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 合同回款(ContractPayment)表数据库访问层
 *
 * @author ak.design 337547038
 * @since 2025-08-09 12:55:22
 */
 @Mapper
public interface ContractPaymentDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ContractPayment queryById(Integer id);

    /**
     * 查询指定行数据
     *@param contractPayment 筛选条件
     *@param extend 扩展参数
     * @return 对象列表
     */
    List<Map<String,Object>> queryAllByLimit(@Param("query") ContractPayment contractPayment,@Param("extend") Map<String,Object> extend);

    /**
     * 统计总行数
     *
     * @param contractPayment 查询条件
     * @return 总行数
     */
    long count(@Param("query") ContractPayment contractPayment,@Param("extend") Map<String,Object> extend);

    /**
     * 新增数据
     *
     * @param contractPayment 实例对象
     * @return 影响行数
     */
    int insert(ContractPayment contractPayment);

    /**
     * 修改数据
     *
     * @param contractPayment 实例对象
     * @return 影响行数
     */
    int updateById(ContractPayment contractPayment);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("id") String[] id, @Param("extend") Map<String, Object> extend);

}

