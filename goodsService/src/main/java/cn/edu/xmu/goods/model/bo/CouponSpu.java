package cn.edu.xmu.goods.model.bo;

import cn.edu.xmu.goods.model.po.CouponSpuPo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponSpu implements VoObject {
    private Long id;

    private Long activityId;

    private Long spuId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public CouponSpu()
    {

    }

    public CouponSpu(CouponSpuPo couponSpuPo)
    {
        id=couponSpuPo.getId();
        activityId=couponSpuPo.getActivityId();
        spuId=couponSpuPo.getSpuId();
        gmtCreate=couponSpuPo.getGmtCreate();
        gmtModified=couponSpuPo.getGmtModified();
    }

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }

    public CouponSpuPo getCouponSpuPo()
    {
        CouponSpuPo couponSpuPo=new CouponSpuPo();
        couponSpuPo.setSpuId(spuId);
        couponSpuPo.setActivityId(activityId);
        return couponSpuPo;
    }
}
