package customer.entity;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import java.io.Serial;


/**
 * 客户操作记录(CustomerOperateRecords)实体类
 *
 * @author ak.design 337547038
 * @since 2025-07-20 11:20:23
 */
@Data
public class CustomerOperateRecords implements Serializable {
    @Serial
    private static final long serialVersionUID = -67492065080308763L;

    private Integer id;
/**
     * 客户id
     */
    private Integer tid;
/**
     * 操作人id
     */
    private Integer userId;

    private Date dataTime;

    private String content;
/**
     * 操作人名称
     */
    private String userName;


}

