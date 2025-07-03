package customer.service;

import customer.entity.FollowRecords;

import java.util.Map;
/**
 * 跟进记录(FollowRecords)表服务接口
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:54:29
 */
public interface FollowRecordsService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    FollowRecords queryById(Integer id);
    
    /**
     * 分页查询
     * @param pages 筛选条件 分页对象
     * @return 查询结果
     */
    Map<String,Object> queryByPage(Map<String,Object> pages);
    /**
     * 新增数据
     *
     * @param followRecords 实例对象
     * @return 实例对象
     */
    FollowRecords insert(FollowRecords followRecords);

    /**
     * 修改数据
     *
     * @param followRecords 实例对象
     * @return 实例对象
     */
    Integer updateById(FollowRecords followRecords);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(String[] id);

}
