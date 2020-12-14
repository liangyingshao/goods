package cn.edu.xmu.goods.mapper;

import cn.edu.xmu.goods.model.po.GoodsSkuPo;
import org.apache.ibatis.annotations.Update;

public interface MyGoodsSkuPoMapper extends GoodsSkuPoMapper{
    @Update({
            "<script>",
            "update table goods_sku",
            "set goods_sku.inventory= goods_sku.inventory-#{inventory}  where goods_sku.id= #{id} and goods_sku.inventory>#{inventory}",
            "</script>"
    })
    int updateQuantityByPrimaryKeySelective(GoodsSkuPo goodsSkuPo);
}
