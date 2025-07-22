package customer.dao;

import customer.entity.CustomerOperateRecords;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 客户操作记录(CustomerOperateRecords)表数据库访问层
 *
 * @author ak.design 337547038
 * @since 2025-07-20 11:20:23
 */
 @Mapper
public interface CustomerOperateRecordsDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    CustomerOperateRecords queryById(Integer id);

    /**
     * 查询指定行数据
     *@param customerOperateRecords 筛选条件
     *@param extend 扩展参数
     * @return 对象列表
     */
    List<Map<String,Object>> queryAllByLimit(@Param("query") CustomerOperateRecords customerOperateRecords,@Param("extend") Map<String,Object> extend);

    /**
     * 统计总行数
     *
     * @param customerOperateRecords 查询条件
     * @return 总行数
     */
    long count(CustomerOperateRecords customerOperateRecords);

    /**
     * 新增数据
     *
     * @param customerOperateRecords 实例对象
     * @return 影响行数
     */
    int insert(CustomerOperateRecords customerOperateRecords);

    /**
     * 修改数据
     *
     * @param customerOperateRecords 实例对象
     * @return 影响行数
     */
    int updateById(CustomerOperateRecords customerOperateRecords);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(String[] id);

}

