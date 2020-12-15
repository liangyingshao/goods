package cn.edu.xmu.activity.mapper;

import cn.edu.xmu.activity.model.po.PresaleActivityPo;
import org.apache.ibatis.annotations.Update;

public interface MyPresaleActivityPoMapper extends PresaleActivityPoMapper{
    @Update({
            "<script>",
            "update table presale_activity",
            "set presale_activity.quantity= presale_activity.quantity-#{quantity}  where presale_activity.id= #{id} and presale_activity.quantity>#{quantity}",
            "</script>"
    })
    int updateQuantityByPrimaryKeySelective(PresaleActivityPo presaleActivityPo);
}
