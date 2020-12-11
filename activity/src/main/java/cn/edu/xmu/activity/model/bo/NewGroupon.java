package cn.edu.xmu.activity.model.bo;

import cn.edu.xmu.activity.model.po.GrouponActivityPo;
import cn.edu.xmu.activity.model.vo.NewGrouponRetVo;
import cn.edu.xmu.goods.model.po.GoodsSpuPo;
import cn.edu.xmu.goods.model.po.ShopPo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.oomall.goods.model.GoodsSpuPoDTO;
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
    GoodsSpuPoDTO goodsSpuPoDTO;
    SimpleShopDTO simpleShopDTO;
    
    public NewGroupon(GrouponActivityPo grouponActivityPo, GoodsSpuPoDTO goodsSpuPoDTO, SimpleShopDTO simpleShopDTO){
        this.id = grouponActivityPo.getId();
        this.name = grouponActivityPo.getName();
        this.goodsSpuPoDTO = goodsSpuPoDTO;
        this.simpleShopDTO = simpleShopDTO;

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
        vo.setSimpleShopDTO(this.simpleShopDTO);
        vo.setGoodsSpuPoDTO(this.goodsSpuPoDTO);
        return vo;
    }
}
