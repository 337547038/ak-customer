package customer.service.impl;

import com.alibaba.fastjson2.JSON;
import customer.config.CustomException;
import customer.entity.Dict;
import customer.entity.LoginLog;
import customer.service.LoginLogService;
import customer.utils.Utils;
import customer.entity.User;
import customer.dao.UserDao;
import customer.service.UserService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
     *
     * @param user 实例对象
     * @return 影响的行数
     */
    @Override
    public Integer updateById(User user) {
        return this.userDao.updateById(user);
        //return this.queryById(user.getId());
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
     * @param user 实体
     * @param ipAddress 登录时的ip地址
     */
    public List<Map<String, Object>> login(User user, String ipAddress) {
        List<Map<String,Object>> list = this.userDao.queryAllByLimit(user, new HashMap<>());
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
            updateUser.setLoginTimer(loginTimer+1);
            updateUser.setIp(ipAddress);
            userDao.updateLogin(updateUser);
            //添加登录日志
            log.setUserId(id);
            log.setStatus(1);
        }else {
            log.setUserId(0); // 登录异常没有id
            log.setStatus(0);
            log.setRemark("密码:"+user.getPassword());
        }
        loginLogService.insert(log);
        return list;
    }
}
