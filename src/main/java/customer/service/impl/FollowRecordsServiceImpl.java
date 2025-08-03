package customer.service.impl;

import com.alibaba.fastjson2.JSON;
import customer.config.CustomException;
import customer.entity.Contact;
import customer.service.ContactService;
import customer.service.UserService;
import customer.utils.Utils;
import customer.entity.FollowRecords;
import customer.dao.FollowRecordsDao;
import customer.service.FollowRecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 跟进记录(FollowRecords)表服务实现类
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:54:29
 */
@Service("followRecordsService")
public class FollowRecordsServiceImpl implements FollowRecordsService {
    @Resource
    private FollowRecordsDao followRecordsDao;

    private final ContactService contactService;
    private final UserService userService;

    public FollowRecordsServiceImpl(ContactService contactService, UserService userService) {
        this.contactService = contactService;
        this.userService = userService;
    }

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public FollowRecords queryById(Integer id) {
        return this.followRecordsDao.queryById(id);
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
        FollowRecords followRecords = JSON.parseObject(JSON.toJSONString(pages), FollowRecords.class);//json字符串转java对象

        Object userId = pages.get("userId");
        String typeStr = String.valueOf(extend.get("search"));

        if (userId == null) {
            if (Objects.equals(typeStr, "child")) {
                // 查看所有下属
                List<String> ids = this.userService.queryUserChild(Utils.getCurrentUserId(), "");
                extend.put("userIds", ids);
            } else {
                // 查看自己的
                extend.put("userId", Utils.getCurrentUserId());
            }
        } else if (userId == Utils.getCurrentUserId()) {
            //　查自己的
            extend.put("userId", Utils.getCurrentUserId());
        } else {
            // 查指定下属的
            boolean isChildUser = this.userService.isChildrenUser((Integer) userId);
            if (!isChildUser) {
                throw new CustomException("请确认你是否具有查看此用户的联系人权限");
            }
            extend.put("userId", userId);
        }
        long total = this.followRecordsDao.count(followRecords,extend);
        List<Map<String, Object>> list = this.followRecordsDao.queryAllByLimit(followRecords, extend);
        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", total);
        return response;
    }

    /**
     * 新增数据
     *
     * @param followRecords 实例对象
     * @return 实例对象
     */
    @Override
    public FollowRecords insert(FollowRecords followRecords) {
        this.followRecordsDao.insert(followRecords);
        //　更新当前联系人的最后跟进和下次跟进时间
        Contact contact = new Contact();
        contact.setId(followRecords.getContactId());
        contact.setLastTime(followRecords.getDateTime());
        contact.setNextTime(followRecords.getNextTime());
        contactService.updateById(contact);
        return followRecords;
    }

    /**
     * 修改数据
     *
     * @param followRecords 实例对象
     * @return 影响的行数
     */
    @Override
    public Integer updateById(FollowRecords followRecords) {
        return this.followRecordsDao.updateById(followRecords);
        //return this.queryById(followRecords.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String[] id) {
        List<String> ids = this.userService.queryUserChild(Utils.getCurrentUserId(), "");
        Map<String, Object> extend = new HashMap<>();
        extend.put("userIds", ids);
        extend.put("userId", Utils.getCurrentUserId());
        return this.followRecordsDao.deleteById(id,extend) > 0;
    }
}
