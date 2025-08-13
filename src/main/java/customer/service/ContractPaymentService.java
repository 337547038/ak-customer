package customer.service;

import customer.entity.ContractPayment;

import java.util.Map;
/**
 * 合同回款(ContractPayment)表服务接口
 *
 * @author ak.design 337547038
 * @since 2025-08-09 12:55:23
 */
public interface ContractPaymentService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ContractPayment queryById(Integer id);
    
    /**
     * 分页查询
     * @param pages 筛选条件 分页对象
     * @return 查询结果
     */
    Map<String,Object> queryByPage(Map<String,Object> pages);
    /**
     * 新增数据
     *
     * @param contractPayment 实例对象
     * @return 实例对象
     */
    ContractPayment insert(ContractPayment contractPayment);

    /**
     * 修改数据
     *
     * @param contractPayment 实例对象
     * @return 实例对象
     */
    Integer updateById(ContractPayment contractPayment);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(String[] id);

    Long total(ContractPayment contractPayment, Map<String, Object> extend);
}
