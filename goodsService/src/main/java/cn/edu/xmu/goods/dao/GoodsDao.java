package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.model.po.ShopPo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.springframework.stereotype.Repository;

@Repository
public class GoodsDao {

    public ReturnObject<ShopPo> modifyShop(Long id, String name)
    {
        return new ReturnObject<>(ResponseCode.OK);
    }


}
