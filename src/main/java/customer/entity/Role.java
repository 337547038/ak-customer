package customer.entity;

import java.io.Serializable;
import lombok.Data;
import java.io.Serial;


/**
 * (Role)实体类
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:54:47
 */
@Data
public class Role implements Serializable {
    @Serial
    private static final long serialVersionUID = -59233596170642050L;

    private Integer id;

    private String name;

    private Integer status;

    private String remark;
/**
     * 权限列表
     */
    private String idList;


}

