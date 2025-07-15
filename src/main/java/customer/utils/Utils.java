package customer.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class Utils {
    /**
     * 公共分页处理方法
     */
    public static Map<String, Object> getPagination(Map<String,Object> pages) {
        //其他参数包含了pageNum当前第几页,pageSize每页分几条,sort排序,columns指定字段
        //根据pageNum计算pageIndex,并对pageSize设置初始值
        JSONObject obj = new JSONObject();
        if (pages.get("extend") != null) {
            obj = JSON.parseObject(JSON.toJSONString(pages.get("extend")));
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
            String token = request.getHeader("Authorization");
            if (token != null) {
                return Integer.valueOf(JWT.decode(token).getAudience().get(0));
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * 将字符串转换为md5值
     *
     * @param string 筛选条件分页对象
     * @return 返回md5值
     */
    public static String convertToMD5(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md.digest(string.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : md5Bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
            //return sb.toString().substring(8, 24);//这个返回16位
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成token
     *
     * @param userId   会员id
     * @param password //密码
     * @param expire   　//　过期时间ms
     * @return token
     */
    public static String getToken(String userId, String password, long expire) {
        Date date = new Date(System.currentTimeMillis() + expire);
        return JWT.create().withAudience(userId)// 将 user id 保存到 token 里面,作为载荷
                .withExpiresAt(date)
                .sign(Algorithm.HMAC256(password));// 以 password 作为 token 的密钥
    }

    /**
     * 图片验证码校验
     *
     * @param code   验证码的值
     * @param codeId 验证码加密的id值
     * @return 是否校验通过
     */
    public static Boolean captchaVerify(String code, String codeId) {
        if (code == null || codeId == null) {
            return false;
        }
        String codeMd5 = convertToMD5(code);
        return Objects.equals(codeMd5, codeId);
    }
    /**
     * 移除字符串最后一个豆号
     *
     * @param str 字符串
     * @return 移除后的内容
     */
    public static String removeLastStr(String str) {
        if (str == null) {
            return null;
        }
        int lastIndex = str.lastIndexOf(",");
        return str.substring(0, lastIndex);
    }
}
