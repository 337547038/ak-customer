package customer.service.impl;

import com.alibaba.fastjson2.JSON;
import customer.entity.Contact;
import customer.service.ContactService;
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

    public FollowRecordsServiceImpl(ContactService contactService) {
        this.contactService = contactService;
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

        long total = this.followRecordsDao.count(followRecords);
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
        return this.followRecordsDao.deleteById(id) > 0;
    }
}
