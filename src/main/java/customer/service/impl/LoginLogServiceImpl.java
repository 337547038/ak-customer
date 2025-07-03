package customer.service.impl;

import com.alibaba.fastjson2.JSON;
import customer.utils.Utils;
import customer.entity.LoginLog;
import customer.dao.LoginLogDao;
import customer.service.LoginLogService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * (LoginLog)表服务实现类
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:54:38
 */
@Service("loginLogService")
public class LoginLogServiceImpl implements LoginLogService {
    @Resource
    private LoginLogDao loginLogDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public LoginLog queryById(Integer id) {
        return this.loginLogDao.queryById(id);
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
        LoginLog loginLog = JSON.parseObject(JSON.toJSONString(pages), LoginLog.class);//json字符串转java对象

        long total = this.loginLogDao.count(loginLog);
        List<Map<String, Object>> list = this.loginLogDao.queryAllByLimit(loginLog, extend);
        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", total);
        return response;
    }

    /**
     * 新增数据
     *
     * @param loginLog 实例对象
     * @return 实例对象
     */
    @Override
    public LoginLog insert(LoginLog loginLog) {
        this.loginLogDao.insert(loginLog);
        return loginLog;
    }

    /**
     * 修改数据
     *
     * @param loginLog 实例对象
     * @return 影响的行数
     */
    @Override
    public Integer updateById(LoginLog loginLog) {
        return this.loginLogDao.updateById(loginLog);
        //return this.queryById(loginLog.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String[] id) {
        return this.loginLogDao.deleteById(id) > 0;
    }
}
