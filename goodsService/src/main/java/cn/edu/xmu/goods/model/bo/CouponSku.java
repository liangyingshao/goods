package cn.edu.xmu.goods.model.bo;

import cn.edu.xmu.goods.model.po.CouponSkuPo;
import cn.edu.xmu.goods.model.po.CouponSkuPo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponSku implements VoObject {
    private Long id;

    private Long activityId;

    private Long skuId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
    

    public CouponSku(CouponSkuPo couponSkuPo)
    {
        id=couponSkuPo.getId();
        activityId=couponSkuPo.getActivityId();
        skuId=couponSkuPo.getSkuId();
        gmtCreate=couponSkuPo.getGmtCreate();
        gmtModified=couponSkuPo.getGmtModified();
    }

    public CouponSku() {

    }

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }

    public CouponSkuPo getCouponSkuPo()
    {
        CouponSkuPo couponSkuPo=new CouponSkuPo();
        couponSkuPo.setSkuId(skuId);
        couponSkuPo.setActivityId(activityId);
        return couponSkuPo;
    }
}
