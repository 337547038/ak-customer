package customer.service.impl;

import com.alibaba.fastjson2.JSON;
import customer.config.CustomException;
import customer.config.PermissionCheck;
import customer.dao.ContactDao;
import customer.dao.CustomerOperateRecordsDao;
import customer.dao.FollowRecordsDao;
import customer.entity.Contact;
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
     * 判断当前登录的用户是否有权限操作当前记录
     *
     * @param id     当前记录id
     * @param detail 可选detail,否则查询更新权限
     * @return true or false
     */
    @Override
    public boolean hasPermission(Integer id, String detail) {
        Map<String, Object> query = new HashMap<>();
        query.put("id", id);
        query.put("userId", Utils.getCurrentUserId());
        List<String> ids = this.userService.queryUserChild(Utils.getCurrentUserId(), "");
        query.put("userIds", ids);
        query.put("search", detail);
        long total = this.customerDao.hasCount(query);
        if (total > 0) {
            return true;
        } else {
            throw new CustomException("记录不存在或请确认是否有权限操作此记录");
        }
    }

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Customer queryById(Integer id) {
        if (hasPermission(id, "detail")) {
            return this.customerDao.queryById(id);
        }
        return null;
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
        if (customer.getStatus() == null) {
            customer.setStatus(1); // 默认查询正常用户
        }
        if (customer.getStatus() == 2 || customer.getStatus() == 3) {
            // 公海/无效
            extend.put("search", "comInvalid");
        }
        String searchType = String.valueOf(extend.get("search"));
        if (customer.getUserId() == null) {
            switch (searchType) {
                case "check":
                case "comInvalid": // 公海和无效
                    break;
                case "myShare": // 我共享的
                    customer.setUserId(Utils.getCurrentUserId());
                    break;
                case "shareWithMe": // 共享给我的
                    customer.setShareUserId(Objects.requireNonNull(Utils.getCurrentUserId()).toString());
                    break;
                case "child": // 查看所有下属
                    List<String> ids = this.userService.queryUserChild(Utils.getCurrentUserId(), "");
                    extend.put("userIds", ids);
                    break;
                default:
                    customer.setUserId(Utils.getCurrentUserId());
                    break;
            }
        } else if (customer.getUserId().equals(Utils.getCurrentUserId())) {
            // 传了等于自己的id时
            customer.setUserId(Utils.getCurrentUserId());
        } else {
            // 查指定下属的，先判断该用户是不是当前登录用户的下属
            boolean isChildUser = this.userService.isChildrenUser(customer.getUserId());
            if (!isChildUser) {
                throw new CustomException("查询异常，请确定有权限查看当前用户");
            }
            extend.put("search", "child");
        }
        if (extend.get("columns") == "diy" && extend.get("diyColumns") != null) {
            Object col = extend.get("diyColumns");
            if (col instanceof String) {
                // 将 String 转换为 List<String>
                List<String> list = new ArrayList<>();
                list.add((String) col);
                extend.put("diyColumns", list);
            }
        }
        long total = this.customerDao.count(customer, extend);
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
        List<Map<String, Object>> list = this.customerDao.exist(hasName);
        if (!list.isEmpty()) {
            throw new CustomException("已存在客户名称:" + customer.getCompany() + ",由" + list.get(0).get("userName") + "正在跟进");
        }
        customer.setUserId(Utils.getCurrentUserId());
        customer.setCreatTime(new Date());
        customer.setStatus(1);
        this.customerDao.insert(customer);
        CustomerOperateRecords cop = new CustomerOperateRecords();
        cop.setTid(customer.getId());
        cop.setUserId(Utils.getCurrentUserId());
        cop.setUserName(Utils.getCurrentUser(""));
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
        List<Map<String, Object>> list = this.customerDao.exist(hasName);
        if (!list.isEmpty()) {
            throw new CustomException("已存在客户名称:" + customer.getCompany() + ",由" + list.get(0).get("userName") + "正在跟进");
        }
        customer.setUpdateTime(new Date());
        if (customer.getUserId().equals(Utils.getCurrentUserId())) {
            // 如果传过来的修改参数有userId时,并等于当前用户直接更新
            return this.customerDao.updateById(customer);
        }
        if (hasPermission(customer.getId(), "")) {
            customer.setUserId(null);
            return this.customerDao.updateById(customer);
        }
        return 0;
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @PermissionCheck(value = {"delCustomer"})
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
        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx")) {
            throw new CustomException(0, "请上传正确的文件格式");
        }
        //0客户名称　1所属区域　2地址　3统一社会信用代码　4电话　5网址　6品牌名称
        //7联系人　8手机　9微信　10ＱＱ　11电子邮件　12职位
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
                customer.setCompany(getCellValue(row.getCell(0)));

                List<Map<String, Object>> list = this.customerDao.exist(customer);
                Contact contact = new Contact();
                contact.setName(getCellValue(row.getCell(7)));
                contact.setPhone(getCellValue(row.getCell(8)));
                contact.setWeixin(getCellValue(row.getCell(9)));
                contact.setWeixin(getCellValue(row.getCell(10)));
                contact.setEmail(getCellValue(row.getCell(11)));
                contact.setPosition(getCellValue(row.getCell(12)));

                if (!list.isEmpty()) {
                    // 存在相同客户名称，检查是不是自己的
                    Integer userId = (Integer) list.get(0).get("userId");
                    if (userId.equals(Utils.getCurrentUserId())) {
                        // 自己的，添加联系人记录
                        contact.setTid((Integer) list.get(0).get("id"));
                        insertContact(contact);
                    }
                } else {
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
                    // 添加联系人
                    contact.setTid(customer.getId());
                    insertContact(contact);
                }
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


    private void insertContact(Contact contact) {
        if (contact.getTid() != null && contact.getName() != null && contact.getPhone() != null) {
            contact.setCreatDate(new Date());
            contact.setDecisionMaker(3);
            this.contactDao.insert(contact);
        }
    }

    /**
     * 移动客户
     *
     * @param params ids需要移动的客户id，userId移动目标对象
     * @param type   类型 toUser移交
     * @return 结果
     */
    @Override
    public boolean moveCustomerByIds(Map<String, Object> params, String type) {
        // 添加客户操作记录
        CustomerOperateRecords records = new CustomerOperateRecords();
        records.setUserId(Utils.getCurrentUserId());
        records.setUserName(Utils.getCurrentUser(""));
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
        // 添加修改权限
        if (!type.equals("toFollow")) {
            List<String> userIds = this.userService.queryUserChild(Utils.getCurrentUserId(), "");
            params.put("hasUserIds", userIds);
            params.put("currentId", Utils.getCurrentUserId());
        }
        return this.customerDao.moveCustomerByIds(params, type);
    }

    /**
     * 分享或取消分享
     *
     * @param params {ids:需要分享的客户id,userId:分享给谁,type=share分享否则为取消分享}
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

    /**
     * 扫描名片一键入库
     *
     * @param params a
     * @return a
     */
    @Override
    public String scanCardInput(Map<String, Object> params) {
        Customer customer = new Customer();
        customer.setCompany(String.valueOf(params.get("company")));
        List<Map<String, Object>> list = this.customerDao.exist(customer);
        if (!list.isEmpty()) {
            // 存在相同客户名称，检查是不是自己的
            Integer userId = (Integer) list.get(0).get("userId");
            if (userId.equals(Utils.getCurrentUserId())) {
                // 自己的，添加联系人记录
                addContact(params, (Integer) list.get(0).get("id"));
                return "联系人新增成功";
            } else {
                // 不是自己的
                return "客户名称：" + params.get("company") + "已由" + list.get(0).get("userName") + "负责跟进";
            }
        } else {
            // 不存在，1新增客户　2新增联系人
            insert(customer);
            addContact(params, customer.getId());
            return "入库成功";
        }
        //return null;
    }

    private void addContact(Map<String, Object> params, Integer customerId) {
        Contact contact = new Contact();
        contact.setName((String) params.get("name"));
        contact.setPhone((String) params.get("phone"));
        contact.setEmail((String) params.get("email"));
        contact.setTid(customerId);
        contact.setCreatDate(new Date());
        contact.setDecisionMaker(3);
        this.contactDao.insert(contact);
    }
}
