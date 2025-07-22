package customer.service;

import customer.entity.CustomerOperateRecords;

import java.util.Map;
/**
 * 客户操作记录(CustomerOperateRecords)表服务接口
 *
 * @author ak.design 337547038
 * @since 2025-07-20 11:20:23
 */
public interface CustomerOperateRecordsService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    CustomerOperateRecords queryById(Integer id);
    
    /**
     * 分页查询
     * @param pages 筛选条件 分页对象
     * @return 查询结果
     */
    Map<String,Object> queryByPage(Map<String,Object> pages);
    /**
     * 新增数据
     *
     * @param customerOperateRecords 实例对象
     * @return 实例对象
     */
    CustomerOperateRecords insert(CustomerOperateRecords customerOperateRecords);

    /**
     * 修改数据
     *
     * @param customerOperateRecords 实例对象
     * @return 实例对象
     */
    Integer updateById(CustomerOperateRecords customerOperateRecords);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(String[] id);

}
