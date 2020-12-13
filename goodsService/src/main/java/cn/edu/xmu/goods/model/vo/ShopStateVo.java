package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.Shop;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员状态VO
 * @author LiangJi3229
 * @date 2020/11/10 18:41
 */
@Data
public class ShopStateVo {

    private Long Code;
    private String name;


    public ShopStateVo(Shop.ShopStatus state){
        Code=Long.valueOf(state.getCode());
        name=state.getDescription();
    }
}