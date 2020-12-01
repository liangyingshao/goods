package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.Comment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 业务: 返回前端的评论
 *
 *
 * @author: 24320182203259 邵良颖
 * Created at: 2020-12-01 15:14
 * version: 1.0
 */
@Data
@ApiModel(description = "评论返回视图")
public class CommentRetVo {
    @ApiModelProperty(value = "评论id")
    private Long id;

    @ApiModelProperty(value = "用户id")
    private Long customerId;

    @ApiModelProperty(value = "商品id")
    private Long goodsSkuId;

    @ApiModelProperty(value = "商品条目id")
    private Long orderitemId;

    @ApiModelProperty(value = "评论类型")
    private Byte type;

    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "评论状态")
    private Byte state;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;

    /**
     * 用Role对象建立Vo对象
     *
     * @author 24320182203281 王纬策
     * @param comment
     * @return RoleRetVo
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    public CommentRetVo(Comment comment) {
        this.id = comment.getId();
        this.customerId = comment.getCustomerId();
        this.goodsSkuId = comment.getGoodsSkuId();
        this.orderitemId = comment.getOrderitemId();
        this.type = comment.getType();
        this.content = comment.getContent();
        this.state = comment.getState();
        this.gmtCreate = comment.getGmtCreate();
        this.gmtModified = comment.getGmtModified();
    }
}
