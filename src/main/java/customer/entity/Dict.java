package customer.entity;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import java.io.Serial;


/**
 * 字典(Dict)实体类
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:54:20
 */
@Data
public class Dict implements Serializable {
    @Serial
    private static final long serialVersionUID = -67581234652837502L;

    private Integer id;

    private String name;
/**
     * 唯一标识
     */
    private String type;

    private Integer status;

    private String remark;
/**
     * 1为系统类型不能删
     */
    private Integer isSys;
/**
     * 创建时间
     */
    private Date dateTime;
/**
     * 字典列表
     */
    private String children;


}

