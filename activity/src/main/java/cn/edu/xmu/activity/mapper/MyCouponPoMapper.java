package cn.edu.xmu.activity.mapper;

import cn.edu.xmu.activity.model.po.CouponPo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;

import java.util.List;

public interface MyCouponPoMapper extends CouponPoMapper{

    @Insert({
            "<script>",
            "insert into coupon(coupon_sn, name, customer_id, activity_id, begin_time, end_time, state, gmt_create, gmt_modified) values ",
            "<foreach collection='couponPos' item='item' index='index' separator=','>",
            "(#{item.coupon_sn}, #{item.name}, #{item.customer_id}, #{item.activity_id}, #{item.begin_time}, #{item.end_time}, #{item.state}, #{item.gmt_create}, #{item.gmt_modified} )",
            "</foreach>",
            "</script>"
    })
    int insertSelectiveBatch(List<CouponPo> couponPos);
}
