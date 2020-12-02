package cn.edu.xmu.goods.controller;

import cn.edu.xmu.goods.model.bo.CouponSpu;
import cn.edu.xmu.goods.model.vo.CouponSpuRetVo;
import cn.edu.xmu.goods.model.vo.CouponSpuVo;
import cn.edu.xmu.goods.model.vo.GoodsSkuVo;
import cn.edu.xmu.goods.model.vo.GoodsSpuCouponRetVo;
import cn.edu.xmu.goods.service.ActivityService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
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

@Api(value = "活动服务", tags = "goods")
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
}
