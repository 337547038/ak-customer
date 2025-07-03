package customer.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

public class Utils {
    /**
     * 公共分页处理方法
     */
    /*public static Map<String, Object> getPagination(Object extend) {
        //其他参数包含了pageNum当前第几页,pageSize每页分几条,sort排序,columns指定字段
        //根据pageNum计算pageIndex,并对pageSize设置初始值
        JSONObject obj = new JSONObject();
        if (extend != null) {
            obj = JSON.parseObject(JSON.toJSONString(extend));
        }
        int pageNum = obj.getIntValue("pageNum", 1);
        int pageSize = obj.getIntValue("pageSize", -1);
        int pageIndex = (pageNum - 1) * pageSize;
        obj.put("pageIndex", pageIndex);
        obj.put("pageSize", pageSize);
        System.out.println(obj);
        return obj;
    }*/
    public static Map<String, Object> getPagination(Object extend) {
        //其他参数包含了pageNum当前第几页,pageSize每页分几条,sort排序,columns指定字段
        //根据pageNum计算pageIndex,并对pageSize设置初始值
        JSONObject obj = new JSONObject();
        if (extend != null) {
            obj = JSON.parseObject(JSON.toJSONString(extend));
        }
        int pageNum = obj.getIntValue("pageNum", 1);
        int pageSize = obj.getIntValue("pageSize", -1);
        int pageIndex = (pageNum - 1) * pageSize;
        obj.put("pageIndex", pageIndex);
        obj.put("pageSize", pageSize);
        //System.out.println(obj);
        return obj;
    }

    /**
     * 返回当前登录用户id
     *
     * @return 返回当前登录用户id
     */
    public static Integer getCurrentUserId() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String userId = request.getHeader("Authorization");
            return Integer.parseInt(userId);
        } catch (Exception e) {
            return 0;
        }
    }
}
