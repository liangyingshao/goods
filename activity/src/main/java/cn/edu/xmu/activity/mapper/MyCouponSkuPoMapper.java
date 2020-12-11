package cn.edu.xmu.activity.mapper;

import cn.edu.xmu.activity.model.po.CouponPo;
import cn.edu.xmu.activity.model.po.CouponSkuPo;
import org.apache.ibatis.annotations.Insert;

import java.util.List;

public interface MyCouponSkuPoMapper extends CouponSkuPoMapper{
    @Insert({
            "<script>",
            "insert into coupon_sku(activity_id, sku_id, gmt_create, gmt_modified) values ",
            "<foreach collection='couponSkuPos' item='item' index='index' separator=','>",
            "(#{item.activity_id}, #{item.sku_id}, #{item.gmt_create}, #{item.gmt_modified} )",
            "</foreach>",
            "</script>"
    })
    int insertSelectiveBatch(List<CouponSkuPo> couponSkuPos);
}
