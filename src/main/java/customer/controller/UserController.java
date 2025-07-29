package customer.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import customer.config.CustomException;
import customer.config.PassToken;
import customer.config.ResponseResult;
import customer.entity.Department;
import customer.entity.Login;
import customer.entity.User;
import customer.service.DepartmentService;
import customer.service.UserService;
import customer.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.annotation.Resource;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static customer.utils.Utils.getToken;

/**
 * (User)表控制层
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:54:55
 */
@Tag(name = "User相关")
@Slf4j
@RestController
@RequestMapping("system/user")
public class UserController {
    /**
     * 服务对象
     */
    @Resource
    private UserService userService;

    @Resource
    private DepartmentService departmentService;


    /**
     * 分页查询
     * 前端传参:
     * * @param pages 筛选条件分页对象
     * {
     * query:{},//查询条件
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
            @Parameter(name = "extend.columns", description = "返回指定查询字段"),
            @Parameter(name = "query", description = "查询条件")
    })
    @PostMapping("list")
    public ResponseEntity<Map<String, Object>> queryByPage(@RequestBody Map<String, Object> pages) {
        return ResponseEntity.ok(this.userService.queryByPage(pages));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param query 主键
     * @return 单条数据
     */
    @Operation(summary = "根据id查询数据")
    @PostMapping("get")
    public ResponseEntity<User> queryById(@RequestBody Map<String, Integer> query) {
        return ResponseEntity.ok(this.userService.queryById(query.get("id")));
    }

    @Operation(summary = "根据id查询数据列表")
    @Parameters({
            @Parameter(name = "ids", description = "查询id,多个使用豆号隔开",required = true)
    })
    @PostMapping("getByIds")
    public ResponseEntity<List<Map<String, Object>>> queryByIds(@RequestBody Map<String, Object> params) {
        if(params.get("ids")==null){
            throw new CustomException("查询id不能为空");
        }
        return ResponseEntity.ok(this.userService.queryByIds((String) params.get("ids")));
    }

    @Operation(summary = "查询当前用户信息")
    @PostMapping("info")
    public ResponseEntity<User> queryByCurrentUser() {
        User user = this.userService.queryById(Utils.getCurrentUserId());
        // 查询直属上级
        Integer tid = user.getTid();
        Integer deptId = user.getDepartmentId();
        Department department = new Department();
        if (deptId != null) {
            department = this.departmentService.queryById(deptId);
        }
        if (tid != null) {
            User leader = this.userService.queryById(tid);
            if (leader.getStatus() == 1) {
                // 正常用户
                user.setTidName(leader.getUserName());
            } else {
                // 当前用户状态不正常时，则从部门负责人中查找
                user.setTidName(getLeaderNameByDeptId(deptId, department.getUserId()));
            }
        } else if (deptId != null) {
            user.setTidName(getLeaderNameByDeptId(deptId, department.getUserId()));
        }
        user.setDepartmentName(department.getName());
        return ResponseEntity.ok(user);
    }

    private String getLeaderNameByDeptId(Integer deptId, Integer userId) {
        if (deptId != null) {
            // 先找出当前部门负责人
            if (userId != null) {
                User leader = this.userService.queryById(userId);
                if (leader.getStatus() == 1) {
                    // 正常用户
                    return leader.getUserName();
                }
            }
        }
        return "";
    }

    /**
     * 新增数据
     *
     * @param user 实体
     * @return 新增结果Id
     */
    @Operation(summary = "新增数据")
    @PostMapping("save")
    public ResponseEntity<Integer> add(@RequestBody User user) {
        user.setCreatTime(new Date());
        user.setLoginTimer(0);
        User result = userService.insert(user);
        return ResponseEntity.ok(result.getId());
    }

    /**
     * 编辑数据
     *
     * @param user 实体
     * @return 影响行数
     */
    @Operation(summary = "编辑数据")
    @PostMapping("edit")
    public ResponseEntity<Integer> edit(@RequestBody User user) {
        user.setUpdateTime(new Date());
        return ResponseEntity.ok(this.userService.updateById(user));
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
        return ResponseEntity.ok(this.userService.deleteById(idList));
    }

    @Operation(summary = "根据用户名和密码登录")
    @Parameter(name = "userName", description = "登录用户名", required = true)
    @Parameter(name = "password", description = "登录密码", required = true)
    @PassToken
    @PostMapping("login")
    public ResponseResult<Map<String, Object>> login(@RequestBody @Validated Login login, HttpServletRequest request) {
        User user = new User();
        user.setStatus(1);
        user.setPassword(login.getPassword());
        user.setUserName(login.getUserName());
        Boolean bool = Utils.captchaVerify(login.getCode(), login.getCodeId());
        if (!bool) {
            return ResponseResult.fail("验证码错误");
        }
        // 获取IP地址
        String ipAddress = request.getRemoteAddr();
        List<Map<String, Object>> list = this.userService.login(user, ipAddress);
        if (list.isEmpty()) {
            return ResponseResult.fail("用户名或密码错误");
        }
        JSONObject obj = JSONObject.from(list.get(0));
        Map<String, Object> map = new HashMap<>();
        map.put("token", getToken(obj.getString("id"), obj.getString("userName"),Utils.EXPIRE_TIME));
        map.put("refreshToken", getToken(obj.getString("id"), obj.getString("userName"), Utils.EXPIRE_TIME * 2));
        map.put("expire_time", Utils.EXPIRE_TIME);
        map.put("id", obj.getString("id"));
        map.put("userName", obj.getString("userName"));
        map.put("roleId", obj.getString("roleId"));
        return ResponseResult.success(map, "登录成功");
    }

    @Operation(summary = "使用refreshToken换取新token")
    @Parameter(name = "refreshToken", description = "token", required = true)
    @PassToken
    @PostMapping("refreshToken")
    public ResponseResult<Map<String, Object>> refreshToken(@RequestBody String params) {
        JSONObject obj = JSONObject.parseObject(params);
        String token = obj.getString("refreshToken");
        if (token == null) {
            throw new CustomException("登录超时，token刷新失败");
        }
        System.out.println(obj);
        String userId;
        try {
            userId = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException e) {
            //log.error("token 解码失败");
            throw new CustomException("登录超时，请重新登录.");
        }
        User user = userService.queryById(Integer.valueOf(userId));
        if (user == null || user.getStatus() == 0) {
            log.error("用户不存在，请重新登录。用户信息:{}", JSON.toJSONString(user));
            throw new CustomException("用户不存在，请重新登录");
        }
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(Utils.HMAC256Secret)).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            //log.error("token 校验失败");
            throw new CustomException("登录超时，请重新登录");
        }
        //生成新token
        Map<String, Object> newToken = new HashMap<>();
        newToken.put("token", getToken(String.valueOf(user.getId()), user.getUserName(), Utils.EXPIRE_TIME));
        newToken.put("refreshToken", getToken(String.valueOf(user.getId()), user.getUserName(), Utils.EXPIRE_TIME * 2));
        newToken.put("expire_time", Utils.EXPIRE_TIME);
        return ResponseResult.success(newToken, "刷新token成功");
    }

}

