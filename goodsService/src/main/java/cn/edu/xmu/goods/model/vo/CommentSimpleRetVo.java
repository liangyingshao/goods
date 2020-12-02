package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.Comment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "评论视图对象")
public class CommentSimpleRetVo {
    @ApiModelProperty(value = "评论id")
    private Long id;

    @ApiModelProperty(value = "用户id")
    private Long customerId;

    @ApiModelProperty(value = "商品id")
    private Long goodsSkuId;

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

    public CommentSimpleRetVo(Comment comment) {
        this.id = comment.getId();
        this.customerId = comment.getCustomerId();
        this.goodsSkuId = comment.getGoodsSkuId();
        this.type = comment.getType();
        this.content = comment.getContent();
        this.state = comment.getState();
        this.gmtCreate = comment.getGmtCreate();
        this.gmtModified = comment.getGmtModified();
    }
}
