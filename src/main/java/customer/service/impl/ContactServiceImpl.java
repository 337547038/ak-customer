package customer.service.impl;

import com.alibaba.fastjson2.JSON;
import customer.config.CustomException;
import customer.service.CustomerService;
import customer.service.UserService;
import customer.utils.Utils;
import customer.entity.Contact;
import customer.dao.ContactDao;
import customer.service.ContactService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.*;
import java.util.stream.Collectors;

/**
 * tid(Contact)表服务实现类
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:51:56
 */
@Service("contactService")
public class ContactServiceImpl implements ContactService {
    @Resource
    private ContactDao contactDao;

    private final UserService userService;

    private final CustomerService customerService;

    public ContactServiceImpl(UserService userService, CustomerService customerService) {
        this.userService = userService;
        this.customerService = customerService;
    }

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Contact queryById(Integer id) {
        // 先查询出客户再判断权限
        Contact contact = contactDao.queryById(id);
        if (!Objects.isNull(contact) && this.customerService.hasPermission(contact.getTid(), "detail")) {
            return contact;
        } else {
            return null;
        }
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
        Contact contact = JSON.parseObject(JSON.toJSONString(pages), Contact.class);//json字符串转java对象

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
            // 查看自己的
            extend.put("userId", Utils.getCurrentUserId());
        } else {
            // 如果传了id时，则查看指定下属
            boolean isChildUser = this.userService.isChildrenUser((Integer) userId);
            if (!isChildUser) {
                throw new CustomException("请确认你是否具有查看此用户的联系人权限");
            }
            extend.put("userId", userId);
        }
        long total = this.contactDao.count(contact, extend);
        List<Map<String, Object>> list = this.contactDao.queryAllByLimit(contact, extend);
        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", total);
        return response;
    }

    /**
     * 新增数据
     *
     * @param contact 实例对象
     * @return 实例对象
     */
    @Override
    public Contact insert(Contact contact) {
        this.contactDao.insert(contact);
        return contact;
    }

    /**
     * 修改数据
     *
     * @param contact 实例对象
     * @return 影响的行数
     */
    @Override
    public Integer updateById(Contact contact) {
        if (this.customerService.hasPermission(contact.getTid(), "")) {
            return this.contactDao.updateById(contact);
        }
        return 0;
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String[] id) {
        // 使用sql查询判断权限删除
        List<String> ids = this.userService.queryUserChild(Utils.getCurrentUserId(), "");
        Map<String, Object> extend = new HashMap<>();
        extend.put("userIds", ids);
        extend.put("userId", Utils.getCurrentUserId());
        return this.contactDao.deleteById(id,extend) > 0;
    }
}
