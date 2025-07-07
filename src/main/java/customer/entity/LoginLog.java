package customer.entity;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import java.io.Serial;


/**
 * (LoginLog)实体类
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:54:38
 */
@Data
public class LoginLog implements Serializable {
    @Serial
    private static final long serialVersionUID = -35636447143362489L;

    private Integer id;

    private String userName;

    private Integer userId;
/**
     * 登录ip
     */
    private String loginIp;
/**
     * 登录时间
     */
    private Date dateTime;

    private String remark;

    private Integer status;


}

