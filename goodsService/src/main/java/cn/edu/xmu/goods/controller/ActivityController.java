package cn.edu.xmu.goods.controller;

import cn.edu.xmu.goods.model.bo.Coupon;
import cn.edu.xmu.goods.model.bo.CouponSpu;
import cn.edu.xmu.goods.model.vo.*;
import cn.edu.xmu.goods.service.ActivityService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Api(value = "活动服务", tags = "activity")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/goods", produces = "application/json;charset=UTF-8")
public class ActivityController {
    private  static  final Logger logger = LoggerFactory.getLogger(ActivityController.class);

    @Autowired
    private ActivityService activityService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 查看优惠活动中的商品
     * @param id
     * @param page
     * @param pageSize
     * @return Object
     */
    @ApiOperation(value = "查看优惠活动中的商品")
    @ApiImplicitParam(paramType = "path",dataType = "Long",name="id",value="活动ID",required = true)
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/couponactivities/{id}/spus")
    @ResponseBody
    public Object getCouponSpuList(@PathVariable Long id,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize)
    {
        ReturnObject<PageInfo<GoodsSpuCouponRetVo>> returnObject=activityService.getCouponSpuList(id,page,pageSize);
        return returnObject;
    }

    /**
     * 管理员为己方某优惠券活动新增限定范围
     * @param shopId
     * @param id
     * @param vo
     * @param bindingResult
     * @param userId
     * @param departId
     * @return Object
     */
    @ApiOperation(value = "管理员为己方某优惠券活动新增限定范围")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization",value = "用户token",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "shopId",value = "店铺id",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "活动id",required = true),
            @ApiImplicitParam(paramType = "body",dataType = "CouponSpuVo",name = "vo",value = "可修改的SKU信息",required = true)
    })
    @Audit
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PostMapping("/shops/{shopId}/couponactivities/{id}/spus")
    @ResponseBody
    public Object createCouponSpu(@PathVariable Long shopId, @PathVariable Long id,
                                  @Validated @RequestBody CouponSpuVo vo, BindingResult bindingResult,
                                  @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                                  @Depart @ApiIgnore @RequestParam(required = false) Long departId)
    {
        logger.debug("createCouponSpu: id = "+ id+" shopId="+shopId+" vo="+vo);
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }
        if(departId!=shopId)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        CouponSpu couponSpu=vo.createCouponSpu();
        couponSpu.setActivityId(id);
        ReturnObject<CouponSpuRetVo> retObject=activityService.createCouponSpu(shopId,couponSpu);
        if (retObject.getData() != null) {
            return Common.decorateReturnObject(retObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }
    }

    /**
     * 店家删除己方某优惠券活动的某限定范围
     * @param shopId
     * @param id
     * @param userId
     * @param departId
     * @return Object
     */
    @ApiOperation(value = "店家删除己方某优惠券活动的某限定范围")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization",value = "用户token",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "shopId",value = "店铺id",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "CouponSpu的id",required = true)
    })
    @Audit
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @DeleteMapping("/shops/{shopId}/couponactivities/{id}/spus")
    @ResponseBody
    public Object deleteCouponSpu(@PathVariable Long shopId, @PathVariable Long id,
                                  @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                                  @Depart @ApiIgnore @RequestParam(required = false) Long departId)
    {
        logger.debug("deleteCouponSpu: id = "+ id+" shopId="+shopId);
        if(departId!=shopId)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        ReturnObject returnObject=activityService.deleteCouponSpu(shopId,id);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 获得优惠卷的所有状态
     * @param userId
     * @return Object
     */
    @ApiOperation(value="获得优惠卷的所有状态")
    @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization",value = "用户token",required = true)
    @Audit
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/coupons/states")
    @ResponseBody
    public Object getcouponState(@LoginUser @ApiIgnore @RequestParam(required = false) Long userId)
    {
        Coupon.State[] states=Coupon.State.class.getEnumConstants();
        List<CouponStateRetVo> stateRetVos=new ArrayList<CouponStateRetVo>();
        for(int i=0;i<states.length;++i)
            stateRetVos.add(new CouponStateRetVo(states[i]));
        return ResponseUtil.ok(new ReturnObject<List>(stateRetVos).getData());
    }

    /**
     * 买家查看优惠券列表
     * @param userId
     * @param state
     * @param page
     * @param pageSize
     * @return Object
     */
    @ApiOperation(value = "买家查看优惠券列表")
    @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization",value = "用户token",required = true)
    @Audit
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/coupons")
    @ResponseBody
    public Object showCoupons(@LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                              @RequestParam(required = false, defaultValue = "1") Integer state,
                              @RequestParam(required = false, defaultValue = "1") Integer page,
                              @RequestParam(required = false, defaultValue = "10") Integer pageSize)
    {
        logger.debug("showCoupons:page="+page+" pageSize="+pageSize);
        if(state!=null&&Coupon.State.getTypeByCode(state)==null)return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
        ReturnObject<PageInfo<CouponRetVo>> returnObject=activityService.showCoupons(userId,state,page,pageSize);
        return returnObject;
    }


    /**
     * 买家使用自己某优惠券
     * @param userId
     * @param id
     * @return Object
     */
    @ApiOperation(value = "买家使用自己某优惠券")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization",value = "用户token",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "优惠券id",required = true)
    })
    @Audit
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PutMapping("/coupons/{id}")
    @ResponseBody
    public Object useCoupon(@LoginUser @ApiIgnore @RequestParam(required = false) Long userId, @PathVariable Long id)
    {
        ReturnObject returnObject=activityService.useCoupon(userId,id);
        return returnObject;
    }

    //据说已废弃
    /**
     * 买家删除自己某优惠券
     * @param userId
     * @param id
     * @return Object
     */
    @ApiOperation(value = "买家删除自己某优惠券")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization",value = "用户token",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "优惠券id",required = true)
    })
    @Audit
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @DeleteMapping("/coupons/{id}")
    @ResponseBody
    public Object deleteCoupon(@LoginUser @ApiIgnore @RequestParam(required = false) Long userId, @PathVariable Long id)
    {
        ReturnObject returnObject=activityService.deleteCoupon(userId,id);
        return returnObject;
    }

    /**
     * 买家领取活动优惠券
     * @param userId
     * @param id
     * @return Object
     */
    @ApiOperation(value = "买家领取活动优惠券")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization",value = "用户token",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "活动id",required = true)
    })
    @Audit
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PostMapping("/couponactivities/{id}/usercoupons")
    @ResponseBody
    public Object getCoupon(@LoginUser @ApiIgnore @RequestParam(required = false) Long userId, @PathVariable Long id)
    {
        ReturnObject<CouponNewRetVo> returnObject=activityService.getCoupon(userId,id);
        return returnObject;
    }

    /**
     * 优惠券退回
     * @param shopId
     * @param id
     * @param userId
     * @param departId
     * @return Object
     */
    @ApiOperation(value = "优惠券退回")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization",value = "用户token",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "shopId",value = "店铺id",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "优惠券id",required = true)
    })
    @Audit
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PutMapping("/shops/{shopId}/coupons/{id}")
    @ResponseBody
    public Object returnCoupon(@PathVariable Long shopId, @PathVariable Long id,
                               @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                               @Depart @ApiIgnore @RequestParam(required = false) Long departId)
    {
        if(shopId!=departId)return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        ReturnObject returnObject=activityService.returnCoupon(id);
        return returnObject;
    }
}
