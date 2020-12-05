package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.GoodsSku;
import cn.edu.xmu.goods.model.bo.GoodsSpu;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 状态VO
 * @author 24320182203254 秦楚彦
 * @date 2020/11/29 00:41
 */
@Data
@ApiModel(value = "查询SKU所有状态视图对象")
public class GoodsSkuStateRetVo {
    private Long Code;

    private String name;
    public GoodsSkuStateRetVo(GoodsSku.State state){
        Code=Long.valueOf(state.getCode());
        name=state.getDescription();
    }
}
