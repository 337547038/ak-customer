package customer.entity;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import java.io.Serial;


/**
 * (User)实体类
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:54:59
 */
@Data
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = -83546180142736631L;

    private Integer id;

    private String userName;

    private String password;
/**
     * 0禁用1正常
     */
    private Integer status;
/**
     * 创建时间
     */
    private Date creatTime;
/**
     * 信息修改时间
     */
    private Date updateTime;
/**
     * 最后登录时间
     */
    private Date lastLogin;
/**
     * 登录次数
     */
    private Integer loginTimer;
/**
     * 最后登录ip地址
     */
    private String ip;

    private String remark;

    private Integer departmentId;
    private String departmentName;
/**
     * 直属上级id
     */
    private Integer tid;
    private String TidName;

    private String phone;

    private String weixin;

    private Integer qq;

    private Integer sex;
/**
     * 角色
     */
    private String roleId;
/**
     * 绑定微信用于登录
     */
    private String bindWX;


}

