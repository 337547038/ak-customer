package customer.service.impl;

import com.alibaba.fastjson2.JSON;
import customer.config.CustomException;
import customer.utils.Utils;
import customer.entity.Customer;
import customer.dao.CustomerDao;
import customer.service.CustomerService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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


    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Customer queryById(Integer id) {
        return this.customerDao.queryById(id);
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
        this.customerDao.insert(customer);
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
        return this.customerDao.updateById(customer);
        //return this.queryById(customer.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String[] id) {
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
}
