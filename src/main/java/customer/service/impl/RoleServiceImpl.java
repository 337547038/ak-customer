package customer.service.impl;

import com.alibaba.fastjson2.JSON;
import customer.utils.Utils;
import customer.entity.Role;
import customer.dao.RoleDao;
import customer.service.RoleService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.*;
import java.util.stream.Collectors;

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
    @Override
    public List<String> queryByIds(String ids) {
        List<Map<String, Object>> list = this.roleDao.queryByIds(ids);
        // 提取content字段
        // 过滤null值
        // 拆分字符串
        // 去除空格
        // 去重
        // 排序（可选）
        // 收集到List
        return list.stream()
                .map(map -> (String) map.get("content")) // 提取content字段
                .filter(Objects::nonNull)                // 过滤null值
                .flatMap(content -> Arrays.stream(content.split(","))) // 拆分字符串
                .map(String::trim)                      // 去除空格
                .distinct()                             // 去重
                .sorted()                               // 排序（可选）
                .collect(Collectors.toList());         // 收集到List
    }
}
