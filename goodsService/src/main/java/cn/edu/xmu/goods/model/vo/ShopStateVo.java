package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.po.ShopPo;
import lombok.Data;

/**
 * 管理员状态VO
 * @author LiangJi3229
 * @date 2020/11/10 18:41
 */
@Data
public class ShopStateVo {
    private Long Code;

    private String name;
    public ShopStateVo(ShopPo.ShopStatus state){
        Code=Long.valueOf(state.getCode());
        name=state.getDescription();
    }
}
