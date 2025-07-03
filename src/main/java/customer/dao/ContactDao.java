package customer.dao;

import customer.entity.Contact;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * tid(Contact)表数据库访问层
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:51:56
 */
 @Mapper
public interface ContactDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Contact queryById(Integer id);

    /**
     * 查询指定行数据
     *@param contact 筛选条件
     *@param extend 扩展参数
     * @return 对象列表
     */
    List<Map<String,Object>> queryAllByLimit(@Param("query") Contact contact,@Param("extend") Map<String,Object> extend);

    /**
     * 统计总行数
     *
     * @param contact 查询条件
     * @return 总行数
     */
    long count(Contact contact);

    /**
     * 新增数据
     *
     * @param contact 实例对象
     * @return 影响行数
     */
    int insert(Contact contact);

    /**
     * 修改数据
     *
     * @param contact 实例对象
     * @return 影响行数
     */
    int updateById(Contact contact);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(String[] id);

}

