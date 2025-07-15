package customer.dao;

import customer.entity.FollowRecords;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 跟进记录(FollowRecords)表数据库访问层
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:54:29
 */
 @Mapper
public interface FollowRecordsDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    FollowRecords queryById(Integer id);

    /**
     * 查询指定行数据
     *@param followRecords 筛选条件
     *@param extend 扩展参数
     * @return 对象列表
     */
    List<Map<String,Object>> queryAllByLimit(@Param("query") FollowRecords followRecords,@Param("extend") Map<String,Object> extend);

    /**
     * 统计总行数
     *
     * @param followRecords 查询条件
     * @return 总行数
     */
    long count(FollowRecords followRecords);

    /**
     * 新增数据
     *
     * @param followRecords 实例对象
     * @return 影响行数
     */
    int insert(FollowRecords followRecords);

    /**
     * 修改数据
     *
     * @param followRecords 实例对象
     * @return 影响行数
     */
    int updateById(FollowRecords followRecords);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("id") String[] id);

}

