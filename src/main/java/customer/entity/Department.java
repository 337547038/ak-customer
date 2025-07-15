package customer.entity;

import java.io.Serializable;
import lombok.Data;
import java.io.Serial;


/**
 * (Department)实体类
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:54:11
 */
@Data
public class Department implements Serializable {
    @Serial
    private static final long serialVersionUID = -33652633844526777L;

    private Integer id;

    private Integer tid;

    private String name;

    private Integer status;

    private String remark;

    private Integer userId;


}

