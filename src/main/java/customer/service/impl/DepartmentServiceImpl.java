package customer.service.impl;

import com.alibaba.fastjson2.JSON;
import customer.config.PermissionCheck;
import customer.utils.Utils;
import customer.entity.Department;
import customer.dao.DepartmentDao;
import customer.service.DepartmentService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.*;
import java.util.stream.Collectors;

/**
 * (Department)表服务实现类
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:54:11
 */
@Service("departmentService")
public class DepartmentServiceImpl implements DepartmentService {
    @Resource
    private DepartmentDao departmentDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    //@PermissionCheck(value = {"/system/dept"})
    @Override
    public Department queryById(Integer id) {
        return this.departmentDao.queryById(id);
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
        Department department = JSON.parseObject(JSON.toJSONString(pages), Department.class);//json字符串转java对象

        long total = this.departmentDao.count(department);
        List<Map<String, Object>> list = this.departmentDao.queryAllByLimit(department, extend);
        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", total);
        return response;
    }

    /**
     * 新增数据
     *
     * @param department 实例对象
     * @return 实例对象
     */
    @PermissionCheck(value = {"/system/dept"})
    @Override
    public Department insert(Department department) {
        this.departmentDao.insert(department);
        return department;
    }

    /**
     * 修改数据
     *
     * @param department 实例对象
     * @return 影响的行数
     */
    @PermissionCheck(value = {"/system/dept"})
    @Override
    public Integer updateById(Department department) {
        return this.departmentDao.updateById(department);
        //return this.queryById(department.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @PermissionCheck(value = {"/system/dept"})
    @Override
    public boolean deleteById(String[] id) {
        // 检查有没子级
        Department dept = new Department();
        dept.setTid(Integer.parseInt(id[0]));
        long total = this.departmentDao.count(dept);
        if (total > 0) {
            return false;
        }
        return this.departmentDao.deleteById(id) > 0;
    }

    /**
     * 根据部门id查询完整部分路径
     *
     * @param id 部门id
     * @return list
     */
    @Cacheable(value = "department",key="'departmentPath_'+#id")
    @Override
    public List<Map<String, Object>> queryDeptFullPath(Integer id) {
        return this.departmentDao.queryDeptFullPath(id);
    }


    /**
     * 根据部门id查询完整部分路径
     *
     * @param id 部门id
     * @return 路径字符串
     */
    @Cacheable(value = "department",key="'departmentPathString_'+#id")
    @Override
    public String queryDeptFullPathString(Integer id) {
        List<Map<String, Object>> list = this.departmentDao.queryDeptFullPath(id);
        return list.stream()
                .map(map -> String.valueOf(map.get("name")))  // 提取 name
                .filter(Objects::nonNull)    // 过滤 null
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        names -> {
                            Collections.reverse(names);  // 逆序
                            return String.join(",", names);  // 拼接成字符串
                        }
                ));
    }
}
