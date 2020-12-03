package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.GoodsSpu;
import lombok.Data;

/**
 * 状态VO
 * @author 24320182203254 秦楚彦
 * @date 2020/11/29 00:41
 */
@Data
public class StateVo {
    private Long Code;

    private String name;
    public StateVo(GoodsSpu.SpuState state){
        Code=Long.valueOf(state.getCode());
        name=state.getDescription();
    }
}
