package customer.service.impl;

import com.alibaba.fastjson2.JSON;
import customer.utils.Utils;
import customer.entity.Role;
import customer.dao.RoleDao;
import customer.service.RoleService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * (Role)表服务实现类
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:54:47
 */
@Service("roleService")
public class RoleServiceImpl implements RoleService {
    @Resource
    private RoleDao roleDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Role queryById(Integer id) {
        return this.roleDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param pages 筛选条件分页对象
     * @return 查询结果
     */
    @Override
    public Map<String, Object> queryByPage(Map<String, Object> pages) {
        Map<String, Object> extend = Utils.getPagination(pages);//处理分页信息
        Role role = JSON.parseObject(JSON.toJSONString(pages), Role.class);//json字符串转java对象

        long total = this.roleDao.count(role);
        List<Map<String, Object>> list = this.roleDao.queryAllByLimit(role, extend);
        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", total);
        return response;
    }

    /**
     * 新增数据
     *
     * @param role 实例对象
     * @return 实例对象
     */
    @Override
    public Role insert(Role role) {
        this.roleDao.insert(role);
        return role;
    }

    /**
     * 修改数据
     *
     * @param role 实例对象
     * @return 影响的行数
     */
    @Override
    public Integer updateById(Role role) {
        return this.roleDao.updateById(role);
        //return this.queryById(role.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String[] id) {
        return this.roleDao.deleteById(id) > 0;
    }
}
