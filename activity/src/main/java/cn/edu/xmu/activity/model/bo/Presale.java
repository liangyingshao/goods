package cn.edu.xmu.activity.model.bo;

import cn.edu.xmu.activity.model.po.PresaleActivityPo;
import cn.edu.xmu.activity.model.vo.PresaleRetVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.oomall.goods.model.GoodsSkuPoDTO;
import cn.edu.xmu.oomall.goods.model.SimpleGoodsSkuDTO;
import cn.edu.xmu.oomall.goods.model.SimpleShopDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * description: Presale
 * date: 2020/12/11 11:48
 * author: 杨铭
 * version: 1.0
 */
@Data
public class Presale implements VoObject {
    Long id;
    String name;
    LocalDateTime beginTime;
    LocalDateTime endTime;
    LocalDateTime payTime;
    SimpleGoodsSkuDTO simpleGoodsSkuDTO;
    SimpleShopDTO simpleShopDTO;

    public Presale(PresaleActivityPo presaleActivityPo,SimpleGoodsSkuDTO simpleGoodsSkuDTO,SimpleShopDTO simpleShopDTO)
    {
        id = presaleActivityPo.getId();
        name = presaleActivityPo.getName();
        beginTime = presaleActivityPo.getBeginTime();
        endTime = presaleActivityPo.getEndTime();
        payTime = presaleActivityPo.getPayTime();
        this.simpleGoodsSkuDTO = simpleGoodsSkuDTO;
        this.simpleShopDTO = simpleShopDTO;

    }

    @Override
    public Object createSimpleVo() {
        return null;
    }

    @Override
    public Object createVo() {

        PresaleRetVo vo = new PresaleRetVo();
        vo.setId(id);
        vo.setName(name);
        vo.setBeginTime(beginTime);
        vo.setPayTime(payTime);
        vo.setEndTime(endTime);
        vo.setGoodsSku(simpleGoodsSkuDTO);
        vo.setShop(simpleShopDTO);
        return vo;
    }


}
