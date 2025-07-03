package customer.entity;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import java.io.Serial;


/**
 * 跟进记录(FollowRecords)实体类
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:54:29
 */
@Data
public class FollowRecords implements Serializable {
    @Serial
    private static final long serialVersionUID = 246902316961109780L;

    private Integer id;
/**
     * 客户id
     */
    private Integer cId;
/**
     * 联系人
     */
    private Integer uId;
/**
     * 用户id
     */
    private Integer userId;
/**
     * 跟进时间
     */
    private Date dateTime;
/**
     * 跟进类型
     */
    private Integer type;
/**
     * 跟进记录客户反馈
     */
    private String remark;


}

