package customer.service.impl;

import com.alibaba.fastjson2.JSON;
import customer.config.CustomException;
import customer.dao.ContactDao;
import customer.dao.CustomerOperateRecordsDao;
import customer.dao.FollowRecordsDao;
import customer.entity.CustomerOperateRecords;
import customer.entity.User;
import customer.service.UserService;
import customer.utils.Utils;
import customer.entity.Customer;
import customer.dao.CustomerDao;
import customer.service.CustomerService;
import customer.service.CustomerOperateRecordsService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;


import jakarta.annotation.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * (Customer)表服务实现类
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:54:02
 */
@Service("customerService")
public class CustomerServiceImpl implements CustomerService {
    @Resource
    private CustomerDao customerDao;
    @Resource
    private FollowRecordsDao followRecordsDao;
    @Resource
    private ContactDao contactDao;
    @Resource
    private CustomerOperateRecordsDao customerOperateRecordsDao;

    private final CustomerOperateRecordsService customerOperateRecordsService;
    private final UserService userService;

    public CustomerServiceImpl(CustomerOperateRecordsService customerOperateRecordsService, UserService userService) {
        this.customerOperateRecordsService = customerOperateRecordsService;
        this.userService = userService;
    }

    /**
     * 通过ID查询单条数据
     *
     * @param query 主键
     * @return 实例对象
     */
    @Override
    public Customer queryById(Map<String, Object> query) {
        query.put("userId",Utils.getCurrentUserId());
        return this.customerDao.queryById(query);
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
        Customer customer = JSON.parseObject(JSON.toJSONString(pages), Customer.class);//json字符串转java对象
        // 根据参数使用不同的查询条件
        Object type = extend.get("type");
        if (type == null || type.equals("")) {
            extend.put("type", "normal");
        }
        String typeStr = extend.get("type").toString();
        switch (typeStr) {
            case "comm":
                // 公海
                customer.setStatus(2);
                break;
            case "invalid":
                //　无效
                customer.setStatus(3);
                break;
            case "check":
                // 查重
                customer.setStatus(1);
                break;
            case "share2": // 共享给我的
                customer.setStatus(1);
                customer.setShareUserId(Objects.requireNonNull(Utils.getCurrentUserId()).toString());
                break;
            case "share": //　我共享的
            case "normal":
            default:
                // 查看自己
                customer.setStatus(1);
                customer.setUserId(Utils.getCurrentUserId());
                break;
        }
        long total = this.customerDao.count(customer);
        List<Map<String, Object>> list = this.customerDao.queryAllByLimit(customer, extend);
        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", total);
        return response;
    }

    /**
     * 新增数据
     *
     * @param customer 实例对象
     * @return 实例对象
     */
    @Override
    public Customer insert(Customer customer) {
        Customer hasName = new Customer();
        hasName.setCompany(customer.getCompany());
        long total = this.customerDao.exist(hasName);
        if (total > 0) {
            throw new CustomException("已存在客户名称:" + customer.getCompany());
        }
        customer.setUserId(Utils.getCurrentUserId());
        customer.setCreatTime(new Date());
        customer.setStatus(1);
        this.customerDao.insert(customer);
        CustomerOperateRecords cop = new CustomerOperateRecords();
        cop.setTid(customer.getId());
        cop.setUserId(Utils.getCurrentUserId());
        cop.setUserName(Utils.getCurrentUserName());
        cop.setDataTime(new Date());
        cop.setContent("创建了该数据记录");
        this.customerOperateRecordsService.insert(cop);
        return customer;
    }

    /**
     * 修改数据
     *
     * @param customer 实例对象
     * @return 影响的行数
     */
    @Override
    public Integer updateById(Customer customer) {
        Customer hasName = new Customer();
        hasName.setCompany(customer.getCompany());
        hasName.setId(customer.getId());
        long total = this.customerDao.exist(hasName);
        if (total > 0) {
            throw new CustomException("已存在客户名称:" + customer.getCompany());
        }
        customer.setUpdateTime(new Date());
        return this.customerDao.updateById(customer);
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String[] id) {
        // 删除跟进信息
        this.followRecordsDao.deleteByCustomerId(id);
        //　删除联系人信息
        this.contactDao.deleteByCustomerId(id);
        //　删除操作记录
        this.customerOperateRecordsDao.deleteByCustomerId(id);
        return this.customerDao.deleteById(id) > 0;
    }

    /**
     * 导入客户
     *
     * @param file xlsx文件
     * @return 结果
     */
    @Override
    public boolean importXlsx(MultipartFile file) {
        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            throw new CustomException(0, "请上传正确的文件格式");
        }
        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
            Iterator<Row> rowIterator = sheet.iterator();
            // 跳过标题行（如果有）
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            //　0客户名称、1所属区域、地址、统一社会信用代码、电话、5网址、品牌名称
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Customer customer = new Customer();
                // 这里应该要过滤重复的用户名 todo
                customer.setCompany(getCellValue(row.getCell(0)));
                customer.setArea(getCellValue(row.getCell(1)));
                customer.setAddress(getCellValue(row.getCell(2)));
                customer.setCode(getCellValue(row.getCell(3)));
                customer.setTel(getCellValue(row.getCell(4)));
                customer.setWeb(getCellValue(row.getCell(5)));
                customer.setBrandName(getCellValue(row.getCell(6)));
                customer.setCreatTime(new Date());
                customer.setUserId(Utils.getCurrentUserId());
                // 读取单元格数据
                /*String name = getCellValue(row.getCell(0));
                Integer age = parseIntCellValue(row.getCell(1));
                */
                this.customerDao.insert(customer);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException(0, "导入失败: " + e.getMessage());
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return null;
        }
    }

    // 获取数字类型
    private Integer parseIntCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            return (int) cell.getNumericCellValue();
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 移动客户
     *
     * @param params 　ids需要移动的客户id，userId移动目标对象
     * @param type   类型 toUser移交
     * @return 结果
     */
    @Override
    public boolean moveCustomerByIds(Map<String, Object> params, String type) {
        // 添加客户操作记录
        CustomerOperateRecords records = new CustomerOperateRecords();
        records.setUserId(Utils.getCurrentUserId());
        records.setUserName(Utils.getCurrentUserName());
        records.setDataTime(new Date());
        switch (type) {
            case "toUser":
                // 找出移交目标名称
                User user = userService.queryById((Integer) params.get("userId"));
                records.setContent("将客户移交给" + user.getUserName() + "(" + params.get("userId") + ")");
                break;
            case "toCom":
                records.setContent("将客户转入公海");
                break;
            case "toFollow":
                records.setContent("从公海或无效客户转入跟进");
                params.put("userId", Utils.getCurrentUserId());
                break;
            case "toInvalid":
                // 设为无效客户
                records.setContent("将客户设为无效客户");
                break;
        }
        String ids = (String) params.get("ids");
        if (ids != null && !ids.isEmpty()) {
            String[] idArray = ids.split(",");
            for (String id : idArray) {
                records.setTid(Integer.valueOf(id));
                customerOperateRecordsService.insert(records);
            }
        }
        return this.customerDao.moveCustomerByIds(params, type);
    }

    /**
     * 分享或取消分享
     *
     * @param params 　{ids:需要分享的客户id,userId:分享给谁,type=share分享否则为取消分享}
     * @return 成功与否
     */
    @Override
    public boolean shareCustomer(Map<String, Object> params) {
        Customer customer = new Customer();
        if (Objects.equals(params.get("type"), "share")) {
            customer.setShareUserId((String) params.get("userId"));
        } else {
            customer.setShareUserId("cancel");
        }
        customer.setUserId(Utils.getCurrentUserId()); // 确保只能操作自己的
        int i = 0;
        String ids = String.valueOf(params.get("ids"));
        if (ids != null && !ids.isEmpty()) {
            String[] idArray = ids.split(",");
            for (String id : idArray) {
                customer.setId(Integer.valueOf(id));
                i = this.customerDao.updateById(customer);
            }
        }
        return i > 0;
    }
}
