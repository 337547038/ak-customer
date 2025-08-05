package customer.config;

import com.alibaba.fastjson2.JSON;
import customer.service.RoleService;
import customer.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * author: 337547038
 * date: 2023-11
 * 作用：
 * 统一拦截打印接口请求入参和响应日志
 * 调用：
 */
@Component
@Aspect
@Slf4j
public class LogAspect {

    private final RoleService roleService;

    public LogAspect(RoleService roleService) {
        this.roleService = roleService;
    }

    @Pointcut("execution(public * customer.controller.*.*(..))")
    public void log() {

    }

    private void printLog(String logType, String format, Object... arguments) {
        if (log.isDebugEnabled()) {
            if (Objects.equals(logType, "info")) {
                log.info(format, arguments);
            } else if (Objects.equals(logType, "error")) {
                log.error(format, arguments);
            } else if (Objects.equals(logType, "debug")) {
                log.error(format, arguments);
            }
        }
    }

    @Around("log()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        String methodName = point.getSignature().toLongString();
        Object[] args = point.getArgs();

        //序列化时过滤掉request和response，在controller使用HttpServletRequest时，这里args会带上request和response
        //添加对MultipartFile的过滤
        Stream<?> stream = ArrayUtils.isEmpty(args) ? Stream.empty() : Arrays.stream(args);
        List<Object> logArgs = stream
                .filter(arg -> (!(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse) && !(arg instanceof MultipartFile)))
                .collect(Collectors.toList());
        Object params = new Object();
        if (!logArgs.isEmpty()) {
            params = logArgs.get(0);
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(attributes)) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        UUID uuid = UUID.randomUUID(); //用于关联响应结果，否则同时多个请求分不清响应是属于哪个请求
        printLog("info", "请求URL：{},\n 请求头信息：{},\n 请求方法全路径：{},\n 请求方法类型：{}, 请求IP：{}, 请求参数：{},\n UUID：{}",
                request.getRequestURL(),
                JSON.toJSONString(getHeaders(request.getHeaderNames(), request)),
                methodName,
                request.getMethod(), request.getRemoteAddr(), JSON.toJSONString(params), uuid);
        Object result = null;
        try {
            result = point.proceed();
        } catch (Exception e) {
            printLog("error", "异常 : {},请求方法类型：{}", e, methodName);
            // 这里不能直接RuntimeException，有些是使用了CustomException自定义的
            //throw new RuntimeException(e);
            throw e;
        }
        printLog("debug", "{}响应 :{}", uuid, JSON.toJSONString(result));
        return result;
    }

    private Map<String, Object> getHeaders(Enumeration<String> headerNames, HttpServletRequest request) {
        Map<String, Object> parameterNameAndValues = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            parameterNameAndValues.put(key, value);
        }
        return parameterNameAndValues;
    }

    @Before("@annotation(customer.config.PermissionCheck)")
    public void before(JoinPoint joinPoint) throws Throwable {
        // 获取方法上的注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        PermissionCheck permissionCheck = method.getAnnotation(PermissionCheck.class);

        // 从token里获取角色id,确保为登录状态
        String roleIds = Utils.getCurrentUser("role");
        Integer userId = Utils.getCurrentUserId();
        if (userId == null) {
            return;
        }
        if ((roleIds == null || roleIds.isEmpty())) {
            throw new CustomException("无权限访问，请确认当前用户已配置了相应角色");
        }
        List<String> userPermissions = this.roleService.queryByIds(roleIds);
        // 校验权限
        if (permissionCheck.logical() == PermissionCheck.Logical.AND) {
            //System.out.println("需要全部权限");
            // 需要全部权限
            if (!new HashSet<>(userPermissions).containsAll(Arrays.asList(permissionCheck.value()))) {
                printLog("error", "无权限访问：{},会员id：{},请求方法类型：{}", permissionCheck.value(), Utils.getCurrentUserId(), method);
                throw new CustomException("无权限访问");
            }
        } else {
            //System.out.println("无权限访问");
            // 只需任意一个权限
            if (Arrays.stream(permissionCheck.value()).noneMatch(userPermissions::contains)) {
                printLog("error", "无权限访问：{},会员id：{},请求方法类型：{}", permissionCheck.value(), Utils.getCurrentUserId(), method);
                throw new CustomException("无权限访问");
            }
        }
    }
}