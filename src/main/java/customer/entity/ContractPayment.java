package customer.entity;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import java.io.Serial;


/**
 * 合同回款(ContractPayment)实体类
 *
 * @author ak.design 337547038
 * @since 2025-08-09 12:55:23
 */
@Data
public class ContractPayment implements Serializable {
    @Serial
    private static final long serialVersionUID = 425600886344861125L;

    private Integer id;
/**
     * 回款编号
     */
    private String code;

    private Integer contractId;
/**
     * 回款金额
     */
    private String money;
/**
     * 回款时间
     */
    private Date datetime;

    private String remark;
/**
     * 创建时间
     */
    private Date creatDate;
/**
     * 回款账户
     */
    private Integer account;
/**
     * 1待确认2领导已确认3没通过
     */
    private Integer status;
/**
     * 附件
     */
    private String files;


}

