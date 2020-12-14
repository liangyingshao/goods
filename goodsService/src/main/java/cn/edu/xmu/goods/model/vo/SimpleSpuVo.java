package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.oomall.goods.model.GoodsSpuPoDTO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * description: SimpleSpuVo
 * date: 2020/12/14 19:54
 * author: 杨铭
 * version: 1.0
 */
@Data
public class SimpleSpuVo {
    Long id;
    String name;
    String goodsSn;
    String imageUrl;
    LocalDateTime gmtCreate;
    LocalDateTime gmtModified;
    Byte disable;

    public SimpleSpuVo(GoodsSpuPoDTO goodsSpuPoDTO){
        id = goodsSpuPoDTO.getId();
        name = goodsSpuPoDTO.getName();
        goodsSn = goodsSpuPoDTO.getGoodsSn();
        imageUrl = goodsSpuPoDTO.getImageUrl();
        gmtCreate = goodsSpuPoDTO.getGmtCreate();
        gmtModified = goodsSpuPoDTO.getGmtModified();
        disable = goodsSpuPoDTO.getDisabled();
    }

}
