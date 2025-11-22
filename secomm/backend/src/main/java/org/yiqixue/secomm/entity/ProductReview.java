package org.yiqixue.secomm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品评价实体类
 * 对应数据库表: product_review
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReview implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 商品ID (外键)
     */
    private Long productId;

    /**
     * 客户ID (外键)
     */
    private Long customerId;

    /**
     * 订单ID (外键)
     */
    private Long orderId;

    /**
     * 评分 (1-5星)
     */
    private Integer rating;

    /**
     * 评价标题
     */
    private String title;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 有帮助的数量
     */
    private Integer helpfulCount;

    /**
     * 是否认证购买 (数据库BIT(1)类型)
     */
    private Boolean verifiedPurchase;

    /**
     * 评价状态 (PENDING, APPROVED, REJECTED)
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime dateCreated;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdated;




    @Override
    public String toString() {
        return "ProductReview{" +
                "id=" + id +
                ", productId=" + productId +
                ", customerId=" + customerId +
                ", orderId=" + orderId +
                ", rating=" + rating +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", helpfulCount=" + helpfulCount +
                ", verifiedPurchase=" + verifiedPurchase +
                ", status='" + status + '\'' +
                ", dateCreated=" + dateCreated +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
