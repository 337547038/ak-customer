package customer.service;

import customer.entity.Contract;

import java.util.Map;
/**
 * 合同信息(Contract)表服务接口
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:53:49
 */
public interface ContractService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Contract queryById(Integer id);
    
    /**
     * 分页查询
     * @param pages 筛选条件 分页对象
     * @return 查询结果
     */
    Map<String,Object> queryByPage(Map<String,Object> pages);
    /**
     * 新增数据
     *
     * @param contract 实例对象
     * @return 实例对象
     */
    Contract insert(Contract contract);

    /**
     * 修改数据
     *
     * @param contract 实例对象
     * @return 实例对象
     */
    Integer updateById(Contract contract);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(String[] id);

    boolean isMeOrChild(Integer contractId);

    Long total(Contract contract, Map<String, Object> extend);
}
