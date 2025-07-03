package customer.entity;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import java.io.Serial;


/**
 * 合同信息(Contract)实体类
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:53:49
 */
@Data
public class Contract implements Serializable {
    @Serial
    private static final long serialVersionUID = -29879805271758920L;

    private Integer id;
/**
     * 客户id
     */
    private Integer cId;
/**
     * 合同编号
     */
    private String num;
/**
     * 签订日期
     */
    private Date creatDate;

    private Integer userId;
/**
     * 合同金额
     */
    private String money;

    private String remark;
/**
     * 合同附件
     */
    private String files;
/**
     * 有效期开始
     */
    private Date startDate;
/**
     * 有效期结束
     */
    private Date endDate;


}

