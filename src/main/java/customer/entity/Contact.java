package customer.entity;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import java.io.Serial;


/**
 * tid(Contact)实体类
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:51:56
 */
@Data
public class Contact implements Serializable {
    @Serial
    private static final long serialVersionUID = 392718767217311946L;

    private Integer id;

    private String name;

    private Integer sex;
/**
     * 手机或固话
     */
    private String tel;
/**
     * 手机
     */
    private String phone;

    private String weixin;

    private String qq;

    private String email;
/**
     * 职务
     */
    private String position;
/**
     * 所属客户
     */
    private Integer tid;
/**
     * 创建时间
     */
    private Date creatDate;
/**
     * 最后一次跟进时间
     */
    private Date lastTime;
/**
     * 下次跟进时间
     */
    private Date nextTime;
/**
     * 是否决策人1是2否3未知
     */
    private Integer decisionMaker;

    private String address;

    private Date birthday;

    private String remark;


}

