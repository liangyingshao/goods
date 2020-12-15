package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.po.ShopPo;
import lombok.Data;

import java.time.format.DateTimeFormatter;

/**
 * description: ShopRetVo
 * date: 2020/12/12 21:30
 * author: 杨铭
 * version: 1.0
 */
@Data
public class ShopRetVo {
    Long id;
    String name;
    Integer state;
    String gmtCreate;
    String gmtModified;

}
