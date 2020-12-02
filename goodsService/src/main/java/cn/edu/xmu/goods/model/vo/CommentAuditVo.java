package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.Comment;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class CommentAuditVo {
    Boolean conclusion;
}
