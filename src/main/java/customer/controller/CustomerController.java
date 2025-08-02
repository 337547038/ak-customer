package customer.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import customer.config.CustomException;
import customer.entity.Customer;
import customer.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.annotation.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * (Customer)表控制层
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:54:02
 */
@Tag(name = "Customer相关")
@RestController
@RequestMapping("customer")
public class CustomerController {
    /**
     * 服务对象
     */
    @Resource
    private CustomerService customerService;

    /**
     * 分页查询
     * 前端传参:
     * * @param pages 筛选条件分页对象
     * {
     * xxxx:xxx,//查询条件
     * extend:{
     * pageNum:1,//当前第几页
     * pageSize:20,//每页多少条记录，默认20。小于0返回全部
     * sort:"id desc"//排序
     * columns:""//返回指定查询字段，如'id,name'
     * }
     * }
     *
     * @return 查询结果
     */
    @Operation(summary = "分页列表")
    @Parameters({
            @Parameter(name = "extend.pageNum", description = "当前第几页"),
            @Parameter(name = "extend.pageSize", description = "每页显示多少条"),
            @Parameter(name = "extend.sort", description = "排序"),
            @Parameter(name = "extend.columns", description = "返回指定扩展字段可选user,contact可联合查询用户名联系人表"),
            @Parameter(name = "extend.search", description = "可选child查询下属,myShare我共享的,shareWithMe共享给我的"),
            @Parameter(name = "userId", description = "根据id查询指定下属用户"),
            @Parameter(name = "status", description = "1正常，2公海，3无效"),
            @Parameter(name = "**", description = "其他查询条件")
    })
    @PostMapping("list")
    public ResponseEntity<Map<String, Object>> queryByPage(@RequestBody Map<String, Object> pages) {
        return ResponseEntity.ok(this.customerService.queryByPage(pages));
    }

    @Operation(summary = "查重分页列表")
    @Parameters({
            @Parameter(name = "extend.pageNum", description = "当前第几页"),
            @Parameter(name = "extend.pageSize", description = "每页显示多少条"),
            @Parameter(name = "extend.sort", description = "排序"),
            @Parameter(name = "extend.columns", description = "返回指定查询字段"),
            @Parameter(name = "keywords", description = "查询关键词")
    })
    @PostMapping("check")
    public ResponseEntity<Map<String, Object>> checkByPage(@RequestBody Map<String, Object> pages) {
        Map<String, Object> keywords = new HashMap<>();
        keywords.put("company", pages.get("keywords"));
        keywords.put("brandName", pages.get("keywords"));
        keywords.put("code", pages.get("keywords"));
        keywords.put("tel", pages.get("keywords"));
        JSONObject obj = JSON.parseObject(JSON.toJSONString(pages.get("extend")));
        obj.put("search", "check");
        keywords.put("extend", obj);
        return ResponseEntity.ok(this.customerService.queryByPage(keywords));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param query 参数
     * @return 单条数据
     */
    @Operation(summary = "根据id查询数据")
    @Parameters({
            @Parameter(name = "id", description = "查询id")
    })
    @PostMapping("get")
    public ResponseEntity<Customer> queryById(@RequestBody Map<String, Object> query) {
        if (query.get("id") == null || query.get("id") == "") {
            throw new CustomException("查询参数不能为空");
        }
        return ResponseEntity.ok(this.customerService.queryById((Integer) query.get("id")));
    }

    /**
     * 新增数据
     *
     * @param customer 实体
     * @return 新增结果Id
     */
    @Operation(summary = "新增数据")
    @PostMapping("save")
    public ResponseEntity<Integer> add(@RequestBody Customer customer) {
        Customer result = customerService.insert(customer);
        return ResponseEntity.ok(result.getId());
    }

    /**
     * 编辑数据
     *
     * @param customer 实体
     * @return 影响行数
     */
    @Operation(summary = "根据id编辑数据")
    @Parameters({
            @Parameter(name = "id", description = "要修改的id", required = true)
    })
    @PostMapping("edit")
    public ResponseEntity<Integer> edit(@RequestBody Customer customer) {
        if (customer.getId() == null || customer.getUserId() == null) {
            throw new CustomException("参数异常");
        }
        return ResponseEntity.ok(this.customerService.updateById(customer));
    }

    /**
     * 删除数据，删除多个时使用豆号分隔
     *
     * @param ids 主键
     * @return 删除是否成功
     */
    @Operation(summary = "根据id删除")
    @Parameter(name = "id", description = "多个id时使用豆号隔开", required = true)
    @PostMapping("delete")
    public ResponseEntity<Boolean> deleteById(@RequestBody Map<String, Object> ids) {
        String string = ids.get("id").toString();
        String[] idList = string.split(",");
        return ResponseEntity.ok(this.customerService.deleteById(idList));
    }

    @Operation(summary = "导入客户")
    @Parameters({
            @Parameter(name = "file", description = "上传的文件", required = true)
    })
    @PostMapping("import")
    public ResponseEntity<Boolean> importXlsx(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new CustomException("请选择文件");
        }
        return ResponseEntity.ok(this.customerService.importXlsx(file));
    }

    @Operation(summary = "移动客户")
    @Parameters({
            @Parameter(name = "ids", description = "要移交的客户id,多个用豆号隔开", required = true),
            @Parameter(name = "type", description = "可选toUser、toCom", required = true),
            @Parameter(name = "userId", description = "toUser时要移交给谁")
    })
    @PostMapping("moveByIds")
    public ResponseEntity<Boolean> toUser(@RequestBody Map<String, Object> params) {
        Object type = params.get("type");
        if (params.get("ids") == null || type == null) {
            throw new CustomException("请按要求提交相应参数");
        }
        if (type == "toUser" && params.get("userId") == null) {
            throw new CustomException("移交时id不能为空");
        }
        return ResponseEntity.ok(this.customerService.moveCustomerByIds(params, (String) type));
    }

    @Operation(summary = "分享或取消分享")
    @Parameters({
            @Parameter(name = "ids", description = "要分享的客户id,多个用豆号隔开", required = true),
            @Parameter(name = "userId", description = "要分享的谁,多个用豆号隔开,为0时所有人可见"),
            @Parameter(name = "type", description = "share时为分享，其他值为取消分享", required = true),
    })
    @PostMapping("share")
    public ResponseEntity<Boolean> toCom(@RequestBody Map<String, Object> params) {
        if (params.get("ids") == null || params.get("ids") == "") {
            throw new CustomException("请选择要分享的客户");
        }
        if (params.get("type") == "share" && params.get("userId") == null) {
            throw new CustomException("请选择分享目标人");
        }
        return ResponseEntity.ok(this.customerService.shareCustomer(params));
    }
}

