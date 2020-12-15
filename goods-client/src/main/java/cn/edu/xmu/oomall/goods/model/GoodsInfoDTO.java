package cn.edu.xmu.oomall.goods.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsInfoDTO implements Serializable {

    private String skuName;

    private Long spuId;

    private String spuName;
    /**
     * 此时单价
     */
    private Long price;
    /**
     * 该sku的所有优惠活动
     */
    private List<CouponInfoDTO> couponActivity;
    /**
     * TODO 现理解为该sku的create时间
     */
    private LocalDateTime addTime;
}
