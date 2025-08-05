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

    /**
     * 根据用户ids返回用户名称,（已分享人根据id回显用户名称）
     *
     * @param params ids
     * @return result list
     */
    @Operation(summary = "根据ids查询数据列表")
    @Parameters({
            @Parameter(name = "ids", description = "查询id,多个使用豆号隔开", required = true)
    })
    @PostMapping("getByIds")
    public ResponseEntity<List<Map<String, Object>>> queryByIds(@RequestBody Map<String, Object> params) {
        if (params.get("ids") == null) {
            throw new CustomException("查询id不能为空");
        }
        return ResponseEntity.ok(this.userService.queryByIds((String) params.get("ids")));
    }

    @Operation(summary = "查询当前用户信息")
    @PostMapping("info")
    public ResponseEntity<User> queryByCurrentUser() {
        User user = this.userService.queryById(Utils.getCurrentUserId());
        // 查询直属上级
        User leader = this.userService.queryById(user.getTid());
        user.setTidName(leader.getUserName());
        // 查询所在部门
        Department department = this.departmentService.queryById(user.getDepartmentId());
        user.setDepartmentName(department.getName());
        return ResponseEntity.ok(user);
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
        User loginUser = this.userService.login(user, ipAddress);
        if (loginUser == null) {
            return ResponseResult.fail("用户名或密码错误");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("token", getToken(loginUser, Utils.EXPIRE_TIME));
        map.put("refreshToken", getToken(loginUser, Utils.EXPIRE_TIME * 2));
        map.put("expire_time", Utils.EXPIRE_TIME);
        map.put("id", loginUser.getId());
        map.put("userName", loginUser.getUserName());
        map.put("roleId", loginUser.getRoleId());
        boolean hasChild = this.userService.hasChild();
        map.put("hasChild", hasChild);
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
        newToken.put("token", getToken(user, Utils.EXPIRE_TIME));
        newToken.put("refreshToken", getToken(user, Utils.EXPIRE_TIME * 2));
        newToken.put("expire_time", Utils.EXPIRE_TIME);
        return ResponseResult.success(newToken, "刷新token成功");
    }


    /**
     * 返回当前用户所有下属
     *
     * @return 返回当前用户所有下属
     */
    @Operation(summary = "查询当前用户所有下属列表")
    @PostMapping("child")
    public ResponseEntity<List<Map<String, Object>>> queryUserChild() {
        return ResponseEntity.ok(this.userService.queryUserChild(Utils.getCurrentUserId()));
    }
}

