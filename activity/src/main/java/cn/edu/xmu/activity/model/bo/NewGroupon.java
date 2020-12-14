package cn.edu.xmu.activity.model.bo;

import cn.edu.xmu.activity.model.po.GrouponActivityPo;
import cn.edu.xmu.activity.model.vo.NewGrouponRetVo;
import cn.edu.xmu.goods.model.vo.SimpleSpuVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.oomall.goods.model.SimpleShopDTO;
import lombok.Data;

/**
 * description: NewGroupon
 * date: 2020/12/10 15:43
 * author: 杨铭
 * version: 1.0
 */
@Data
public class NewGroupon  implements VoObject {

    Long id;
    String name;
    SimpleSpuVo goodsSpu;
    SimpleShopDTO shop;
    
    public NewGroupon(GrouponActivityPo grouponActivityPo, SimpleSpuVo goodsSpu, SimpleShopDTO simpleShopDTO){
        this.id = grouponActivityPo.getId();
        this.name = grouponActivityPo.getName();
        this.goodsSpu = goodsSpu;
        this.shop = simpleShopDTO;

    }

    @Override
    public Object createSimpleVo() {
        return null;
    }

    @Override
    public Object createVo() {
        NewGrouponRetVo vo = new NewGrouponRetVo();
        vo.setId(this.id);
        vo.setName(this.name);
        vo.setShop(this.shop);
        vo.setGoodsSpu(this.goodsSpu);
        return vo;
    }
}
