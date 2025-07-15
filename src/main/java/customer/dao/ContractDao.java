package customer.dao;

import customer.entity.Contract;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 合同信息(Contract)表数据库访问层
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:53:49
 */
 @Mapper
public interface ContractDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Contract queryById(Integer id);

    /**
     * 查询指定行数据
     *@param contract 筛选条件
     *@param extend 扩展参数
     * @return 对象列表
     */
    List<Map<String,Object>> queryAllByLimit(@Param("query") Contract contract,@Param("extend") Map<String,Object> extend);

    /**
     * 统计总行数
     *
     * @param contract 查询条件
     * @return 总行数
     */
    long count(Contract contract);

    /**
     * 新增数据
     *
     * @param contract 实例对象
     * @return 影响行数
     */
    int insert(Contract contract);

    /**
     * 修改数据
     *
     * @param contract 实例对象
     * @return 影响行数
     */
    int updateById(Contract contract);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("id") String[] id);

}

