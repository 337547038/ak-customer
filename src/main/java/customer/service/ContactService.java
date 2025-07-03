package customer.service;

import customer.entity.Contact;

import java.util.Map;
/**
 * tid(Contact)表服务接口
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:51:56
 */
public interface ContactService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Contact queryById(Integer id);
    
    /**
     * 分页查询
     * @param pages 筛选条件 分页对象
     * @return 查询结果
     */
    Map<String,Object> queryByPage(Map<String,Object> pages);
    /**
     * 新增数据
     *
     * @param contact 实例对象
     * @return 实例对象
     */
    Contact insert(Contact contact);

    /**
     * 修改数据
     *
     * @param contact 实例对象
     * @return 实例对象
     */
    Integer updateById(Contact contact);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(String[] id);

}
