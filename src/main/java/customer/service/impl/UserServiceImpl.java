package customer.service.impl;

import com.alibaba.fastjson2.JSON;
import customer.config.CustomException;
import customer.entity.LoginLog;
import customer.service.LoginLogService;
import customer.utils.Utils;
import customer.entity.User;
import customer.dao.UserDao;
import customer.service.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.*;
import java.util.stream.Collectors;


/**
 * (User)表服务实现类
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:55:00
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;

    private final LoginLogService loginLogService;

    public UserServiceImpl(LoginLogService loginLogService) {
        this.loginLogService = loginLogService;
    }


    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public User queryById(Integer id) {
        return this.userDao.queryById(id);
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
        User user = JSON.parseObject(JSON.toJSONString(pages), User.class);//json字符串转java对象

        long total = this.userDao.count(user);
        List<Map<String, Object>> list = this.userDao.queryAllByLimit(user, extend);
        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", total);
        return response;
    }

    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    @Override
    public User insert(User user) {
        User hasUser = new User();
        hasUser.setUserName(user.getUserName());
        long total = this.userDao.count(hasUser);
        if (total > 0) {
            // 存在相同key
            throw new CustomException("已存在用户名:" + user.getUserName());
        }
        this.userDao.insert(user);
        return user;
    }

    /**
     * 修改数据
     * 修改数据时同时将tokenVerify缓存失效
     *
     * @param user 实例对象
     * @return 影响的行数
     */
    @CacheEvict(value = "tokenVerify", key = "#user.id")
    @Override
    public Integer updateById(User user) {
        return this.userDao.updateById(user);
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String[] id) {
        return this.userDao.deleteById(id) > 0;
    }

    /**
     * 根据用户名和密码登录
     *
     * @param user      实体
     * @param ipAddress 登录时的ip地址
     */
    @Override
    public List<Map<String, Object>> login(User user, String ipAddress) {
        List<Map<String, Object>> list = this.userDao.queryAllByLimit(user, new HashMap<>());
        LoginLog log = new LoginLog();
        log.setUserName(user.getUserName());
        log.setLoginIp(ipAddress);
        log.setDateTime(new Date());
        if (!list.isEmpty()) {
            //更新登录信息
            Map<String, Object> listObj = list.get(0);
            Integer loginTimer = (Integer) listObj.get("loginTimer");
            Integer id = (Integer) listObj.get("id");
            User updateUser = new User();
            updateUser.setId(id);
            updateUser.setLastLogin(new Date());
            updateUser.setLoginTimer(loginTimer + 1);
            updateUser.setIp(ipAddress);
            userDao.updateLogin(updateUser);
            //添加登录日志
            log.setUserId(id);
            log.setStatus(1);
        } else {
            log.setUserId(0); // 登录异常没有id
            log.setStatus(0);
            log.setRemark("密码:" + user.getPassword());
        }
        loginLogService.insert(log);
        return list;
    }

    @Override
    public List<Map<String, Object>> queryByIds(String ids) {
        return this.userDao.queryByIds(ids.split(","));
    }

    /**
     * 用于token校验，token一旦生成可通过修改会员状态使其失效
     * @param userId id
     * @return true or false
     */
    @Cacheable(value = "tokenVerify", key = "#userId")
    @Override
    public boolean tokenVerify(Integer userId) {
        User user = new User();
        user.setId(userId);
        user.setStatus(1);
        return this.userDao.count(user) > 0;
    }
    /**
     * 根据用户id返回所有子级
     *
     * @param userId 用户id
     * @return 子级id数组
     */
    @Override
    // 整个缓存
    @Cacheable(value = "userChild", key = "#userId")
    public List<Map<String, Object>> queryUserChild(Integer userId) {
        return this.userDao.queryUserChild(userId);
    }

    @Override
    @Cacheable(value = "userChild", key = "#userId+'String'")
    public List<String> queryUserChild(Integer userId, String type) {
        List<Map<String, Object>> list = this.userDao.queryUserChild(userId);
        // 提取所有id并转为List<String>
        return list.stream()
                .map(map -> String.valueOf(map.get("id"))) // 确保转换为String
                .collect(Collectors.toList());
    }

    /**
     * 判断指定用户id是否属于当前登录用户的下属
     *
     * @param userId 指定id
     * @return 是否
     */
    @Override
    @Cacheable(value = "userChild", key = "#userId+'ChildUser'")
    public boolean isChildrenUser(Integer userId) {
        List<Map<String, Object>> list = this.userDao.queryUserChild(Utils.getCurrentUserId());
        if (list == null || userId == null) {
            return false;
        }
        return list.stream()
                .filter(map -> map.get("id") instanceof Integer)
                .anyMatch(map -> Objects.equals(map.get("id"), userId));
    }

    /**
     * 当前登录用户是否有下属
     *
     * @return result
     */
    @Override
    public boolean hasChild() {
        User user = new User();
        user.setTid(Utils.getCurrentUserId());
        user.setStatus(1);
        long total = this.userDao.count(user);
        return total > 0;
    }
}
