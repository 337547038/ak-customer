package customer.controller;

import customer.entity.LoginLog;
import customer.service.LoginLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.annotation.Resource;

import java.util.Map;

/**
 * (LoginLog)表控制层
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:54:38
 */
@Tag(name = "LoginLog相关")
@RestController
@RequestMapping("system/loginLog")
public class LoginLogController {
    /**
     * 服务对象
     */
    private final LoginLogService loginLogService;

    public LoginLogController(LoginLogService loginLogService) {
        this.loginLogService = loginLogService;
    }

    /**
     * 分页查询
     * 前端传参:
     * * @param pages 筛选条件分页对象
     * {
     *     query:{},//查询条件
     *     extend:{
     *         pageNum:1,//当前第几页
     *         pageSize:20,//每页多少条记录，默认20。小于0返回全部
     *         sort:"id desc"//排序
     *         columns:""//返回指定查询字段，如'id,name'
     *     }
     * }
     * @return 查询结果
     */
    @Operation(summary ="分页列表")
    @Parameters({
            @Parameter(name = "extend.pageNum",description = "当前第几页"),
            @Parameter(name = "extend.pageSize",description = "每页显示多少条"),
            @Parameter(name = "extend.sort",description = "排序"),
            @Parameter(name = "extend.columns",description = "返回指定查询字段"),
            @Parameter(name = "query",description = "查询条件")
    })
    @PostMapping("list")
    public ResponseEntity<Map<String, Object>> queryByPage(@RequestBody Map<String, Object> pages) {
        return ResponseEntity.ok(this.loginLogService.queryByPage(pages));
    }

    /**
     * 通过主键查询单条数据
     *
     *@param query 主键
     * @return 单条数据
     */
    @Operation(summary ="根据id查询数据")
    @PostMapping("get")
    public ResponseEntity<LoginLog> queryById(@RequestBody Map<String, Integer> query) {
        return ResponseEntity.ok(this.loginLogService.queryById(query.get("id")));
    }

    /**
     * 新增数据
     *
     * @param loginLog 实体
     * @return 新增结果Id
     */
/*    @Operation(summary ="新增数据")
    @PostMapping("save")
    public ResponseEntity<Integer> add(@RequestBody LoginLog loginLog) {
        LoginLog result = loginLogService.insert(loginLog);
        return ResponseEntity.ok(result.getId());
    }*/

    /**
     * 编辑数据
     *
     * @param loginLog 实体
     * @return 影响行数
     */
    /*@Operation(summary ="编辑数据")
    @PostMapping("edit")
    public ResponseEntity<Integer> edit(@RequestBody LoginLog loginLog) {
        return ResponseEntity.ok(this.loginLogService.updateById(loginLog));
    }
*/
    /**
     * 删除数据，删除多个时使用豆号分隔
     *
     * @param ids 主键
     * @return 删除是否成功
     */
    @Operation(summary ="根据id删除")
    @Parameter(name = "id",description = "多个id时使用豆号隔开",required = true)
    @PostMapping("delete")
    public ResponseEntity<Boolean> deleteById(@RequestBody Map<String,Object> ids) {
        String string = ids.get("id").toString();
        String[] idList = string.split(",");
        return ResponseEntity.ok(this.loginLogService.deleteById(idList));
    }

}

