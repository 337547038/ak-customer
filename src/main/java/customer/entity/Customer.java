package customer.entity;

import java.io.Serializable;
import lombok.Data;
import java.io.Serial;
import java.util.Date;


/**
 * (Customer)实体类
 *
 * @author ak.design 337547038
 * @since 2025-07-03 13:54:02
 */
@Data
public class Customer implements Serializable {
    @Serial
    private static final long serialVersionUID = 234916952922431894L;

    private Integer id;
/**
     * 公司名称
     */
    private String company;
/**
     * 产品品牌名称
     */
    private String brandName;
/**
     * 合作类型
     */
    private Integer type;
/**
     * 所属区域
     */
    private String area;
/**
     * 公司地址
     */
    private String address;
/**
     * 行业分类
     */
    private Integer industry;
/**
     * 统一社会信用代码
     */
    private String code;
/**
     * 相关附件
     */
    private String files;
/**
     * 来源
     */
    private Integer source;
/**
     * 电话
     */
    private String tel;
/**
     * 网址
     */
    private String web;
/**
     * 所属人员
     */
    private Integer userId;
/**
     * 创建时间
     */
    private Date creatTime;
/**
     * 更新时间
     */
    private Date updateTime;


}

