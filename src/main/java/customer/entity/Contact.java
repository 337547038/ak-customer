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
    private Integer phone;

    private String weixin;

    private Integer qq;

    private String email;
/**
     * 职务
     */
    private Integer position;
/**
     * 所属客户
     */
    private Integer tid;
/**
     * 创建时间
     */
    private Date creatDate;
/**
     * 下次跟进时间
     */
    private Date nextTime;


}

