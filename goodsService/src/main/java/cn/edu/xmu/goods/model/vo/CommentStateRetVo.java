package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.Comment;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel
public class CommentStateRetVo {
    private Long Code;
    private String name;
    public CommentStateRetVo(Comment.State state){
        Code=Long.valueOf(state.getCode());
        name=state.getDescription();
    }
}
