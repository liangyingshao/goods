package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.Comment;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class CommentVo {
    @NotNull(message = "type不得为空")
    @Range(min = 0, max = 2, message = "错误的requestType数值")
    private Byte type;

    @NotNull(message = "content不得为空")
    private String content;

    public Comment createComment()
    {
        Comment comment=new Comment();
        comment.setContent(content);
        comment.setType(type);
        return comment;
    }
}
