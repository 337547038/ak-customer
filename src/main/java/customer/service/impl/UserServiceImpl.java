package customer.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import customer.config.CustomException;
import customer.config.PermissionCheck;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
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

    private final RestTemplate restTemplate;

    public UserServiceImpl(LoginLogService loginLogService, RestTemplate restTemplate) {
        this.loginLogService = loginLogService;
        this.restTemplate = restTemplate;
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
    // @PermissionCheck(value = {"/system/user"})
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
    @PermissionCheck(value = {"/system/user"})
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
    //@PermissionCheck(value = {"/system/user"})
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
    @PermissionCheck(value = {"/system/user"})
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
    public User login(User user, String ipAddress) {
        List<Map<String, Object>> list = this.userDao.queryAllByLimit(user, new HashMap<>());
        LoginLog log = new LoginLog();
        // log.setUserName(user.getUserName()); // 扫码登录时这里没有值
        log.setLoginIp(ipAddress);
        log.setDateTime(new Date());
        User loginUser = new User();
        if (!list.isEmpty()) {
            //更新登录信息
            loginUser = JSON.parseObject(JSON.toJSONString(list.get(0)), User.class);//json字符串转java对象
            User updateUser = new User();
            updateUser.setId(loginUser.getId());
            updateUser.setLastLogin(new Date());
            updateUser.setLoginTimer(loginUser.getLoginTimer() + 1);
            updateUser.setIp(ipAddress);
            userDao.updateLogin(updateUser);
            //添加登录日志
            log.setUserName(loginUser.getUserName());
            log.setUserId(loginUser.getId());
            log.setStatus(1);
        } else {
            log.setUserId(0); // 登录异常没有id
            log.setStatus(0);
            if (user.getBindWX() != null) {
                log.setRemark("当前用户没有绑定微信：" + user.getBindWX());
            } else {
                log.setRemark("密码:" + user.getPassword());
            }
        }
        loginLogService.insert(log);
        return loginUser;
    }

    // 带查询参数的GET
    private String getWithParams(String code) {
        URI uri = UriComponentsBuilder.fromUriString("https://api.weixin.qq.com/sns/oauth2/access_token")
                .queryParam("appid", "APPID")
                .queryParam("secret", "secret")
                .queryParam("code", code)
                .queryParam("grant_type", "authorization_code")
                .build()
                .toUri();
        return restTemplate.getForObject(uri, String.class);
    }

    /**
     * 微信扫码登录
     *
     * @param query     code相关参数
     * @param ipAddress ip
     * @return result
     */
    @Override
    public User scanLogin(Map<String, Object> query, String ipAddress) {
        JSONObject result = JSONObject.parseObject(getWithParams(query.get("code").toString()));
        if (result.get("openid") != null) {
            User user = new User();
            user.setBindWX(result.getString("openid"));
            user.setStatus(1);
            return login(user, ipAddress);
        } else {
            // 获取失败
            throw new CustomException(result.get("errmsg").toString());
        }
    }

    @Override
    public Boolean bindWX(Map<String, Object> params) {
        User user = new User();
        user.setId(Utils.getCurrentUserId());
        if (params.get("code") != null) {
            // 绑定
            JSONObject result = JSONObject.parseObject(getWithParams(params.get("code").toString()));
            if (result.get("openid") != null) {
                user.setBindWX(result.getString("openid"));
                return this.userDao.updateById(user) > 0;
            } else {
                // 获取失败
                throw new CustomException(result.get("errmsg").toString());
            }
        } else {
            // 解除
            user.setBindWX("null");
            return this.userDao.updateById(user) > 0;
        }
    }

    @Override
    public List<Map<String, Object>> queryByIds(String ids) {
        return this.userDao.queryByIds(ids.split(","));
    }

    /**
     * 用于token校验，token一旦生成可通过修改会员状态使其失效,每次请求会调用一次
     *
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
     * 根据id获取当前会员所有上级
     *
     * @param userId 当前用户id
     * @return 所有上级
     */
    @Override
    @Cacheable(value = "userChild", key = "'fatherList_'+#userId")
    public String queryUserFather(Integer userId) {
        List<Map<String, Object>> list = this.userDao.queryUserChildFather(userId, false);
        return list.stream()
                .map(map -> String.valueOf(map.get("userName")))  // 提取 name
                .filter(Objects::nonNull)    // 过滤 null
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        names -> {
                            Collections.reverse(names);  // 逆序
                            return String.join(",", names);  // 拼接成字符串
                        }
                ));
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
        return this.userDao.queryUserChildFather(userId, true);
    }

    @Override
    @Cacheable(value = "userChild", key = "#userId+'String'")
    public List<String> queryUserChild(Integer userId, String type) {
        List<Map<String, Object>> list = this.userDao.queryUserChildFather(userId, true);
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
        List<Map<String, Object>> list = queryUserChild(Utils.getCurrentUserId());
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

    /**
     * 统计当前用户及下属的客户数量及合合
     *
     * @return list
     */
    @Cacheable(value = "analysis", key = "T(customer.utils.Utils).getCurrentUserId()+'_userCustomer'")
    @Override
    public List<Map<String, Object>> userCustomer() {
        Integer userId = Utils.getCurrentUserId();
        List<String> list = queryUserChild(userId, "");
        return this.userDao.queryUserCustomerNum(userId, list);
    }

    /**
     * 统计员工跟进分析
     *
     * @return list
     */
    @Cacheable(value = "analysis", key = "T(customer.utils.Utils).getCurrentUserId()+'_follow'")
    @Override
    public List<Map<String, Object>> queryUserFollow() {
        Integer userId = Utils.getCurrentUserId();
        List<String> list = queryUserChild(userId, "");
        return this.userDao.queryUserFollow(userId, list);
    }

    /**
     * 分析统计合同排行
     *
     * @return list
     */
    @Cacheable(value = "analysis", key = "T(customer.utils.Utils).getCurrentUserId()+'_contract'")
    @Override
    public List<Map<String, Object>> queryUserContract() {
        Integer userId = Utils.getCurrentUserId();
        List<String> list = queryUserChild(userId, "");
        return this.userDao.queryUserContract(userId, list);
    }
}
