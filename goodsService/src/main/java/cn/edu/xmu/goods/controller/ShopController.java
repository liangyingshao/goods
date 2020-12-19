package cn.edu.xmu.goods.controller;

import cn.edu.xmu.goods.model.bo.Shop;
import cn.edu.xmu.goods.model.vo.ShopStateVo;
import cn.edu.xmu.goods.model.vo.ShopVo;
import cn.edu.xmu.goods.service.ShopService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * description: ShopController
 * date: 2020/12/10 13:09
 * author: 杨铭
 * version: 1.0
 */
@Api(value = "店铺服务", tags = "goods")
@RestController /*Restful的Controller对象*/
//@RequestMapping(value = "/goods",produces = "application/json;charset=UTF-8")
@RequestMapping(value = "",produces = "application/json;charset=UTF-8")
public class ShopController {


    @Autowired
    ShopService shopService;


    private  static  final Logger logger = LoggerFactory.getLogger(ShopController.class);

    @Autowired
    private HttpServletResponse httpServletResponse;


    /**
     * description: modifyShop
     * version: 1.0
     * date: 2020/11/29 23:15
     * author: 杨铭
     *
     * @param shopid Long
     * @param shopVo ShopVo
     * @return java.lang.Object
     */
    @ApiOperation(value="店家修改店铺信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "String", name = "name", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopid", required = true)

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PutMapping("shops/{shopid}")
    @ResponseBody
    public Object modifyShop(@PathVariable @Depart Long shopid, @Validated @RequestBody ShopVo shopVo, BindingResult bindingResult) {

        Object retObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != retObject) {
            logger.debug("validate fail");
            return retObject;
        }

        ReturnObject returnObject = shopService.modifyShop(shopid,shopVo.getName());
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }


    /**
     * description: 申请店铺
     * version: 1.0
     * date: 2020/12/3 8:41
     * author: 杨铭
     *
     * @param id 用户id
     * @param shopVo 店铺名称
     * @return java.lang.Object
     */
    @ApiOperation(value = "店家申请店铺")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "String", name = "name", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 908, message = "用户已经有店铺")
    })
    @Audit
    @PostMapping("/shops")
    @ResponseBody
    public Object addShop(@LoginUser Long id, @Validated @RequestBody ShopVo shopVo, BindingResult bindingResult){

        Object retObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != retObject) {
            logger.debug("validate fail");
            return retObject;
        }

        ReturnObject<VoObject> returnObject =  shopService.addShop(id,shopVo.getName());
        if (returnObject.getCode() == ResponseCode.OK) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }

    }


    /**
     * description: deleteShop
     * version: 1.0
     * date: 2020/12/1 11:50
     * author: 杨铭
     *
     * @param shopId 店铺id
     * @return java.lang.Object
     */
    @ApiOperation(value="管理员或店家关闭店铺")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",required = true,name = "authorization"),
            @ApiImplicitParam(paramType = "path",dataType = "Integer",required = true,name="shopId")
    })
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @Audit
    @DeleteMapping("/shops/{shopId}")
    @ResponseBody
    public Object deleteShop(@PathVariable Long shopId) {
        ReturnObject<VoObject> returnObject =  shopService.deleteShop(shopId);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**
     * description: getshopState
     * version: 1.0
     * date: 2020/12/3 9:02
     * author: 杨铭
     *
     * @param
     * @return java.lang.Object
     */
    @ApiOperation(value="获得店铺的所有的状态")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/shops/states")
    @ResponseBody
    public Object getshopState() {
        Shop.ShopStatus[] states= Shop.ShopStatus.class.getEnumConstants();
        List<ShopStateVo> stateVos=new ArrayList<ShopStateVo>();
        for(int i=0;i<states.length;i++){
            stateVos.add(new ShopStateVo(states[i]));
        }
        return ResponseUtil.ok(new ReturnObject<List>(stateVos).getData());
    }


    /**
     * description: auditShop
     * version: 1.0
     * date: 2020/12/2 17:29
     * author: 杨铭
     *
     * @param id 操作的店铺id
     * @param conclusion 审核是否通过
     * @return java.lang.Object
     */
    @ApiOperation(value="平台管理员审核店铺信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Integer",name="shopId",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Integer",name="id",required = true),
            @ApiImplicitParam(paramType = "body",dataType = "String",name="conclusion",required = true)
    })
    @ApiResponse(code=0,message = "成功")
    @PutMapping("/shops/{shopId}/newshops/{id}/audit")
    @Audit
    @ResponseBody
    public Object auditShop(@PathVariable Long id,@RequestBody boolean conclusion){
        ReturnObject<VoObject> returnObject =  shopService.auditShop(id,conclusion);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**
     * description: onshelfShop
     * version: 1.0
     * date: 2020/12/1 12:13
     * author: 杨铭
     *
     * @param shopId 店铺id
     * @return java.lang.Object
     */
    @ApiOperation(value="管理员上线店铺")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Integer",name="shopId",required = true)
    })
    @ApiResponse(code=0,message = "成功")
    @Audit
    @PutMapping("/shops/{shopId}/onshelves")
    @ResponseBody
    public Object onshelfShop(@PathVariable Long shopId) {
        ReturnObject<VoObject> returnObject =  shopService.onshelfShop(shopId);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**
     * description: offshelfShop
     * version: 1.0
     * date: 2020/12/1 12:16
     * author: 杨铭
     *
     * @param shopId
     * @return java.lang.Object
     */
    @ApiOperation(value="管理员下线店铺")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Integer",name="shopId",required = true)
    })
    @ApiResponse(code=0,message = "成功")
    @Audit
    @PutMapping("/shops/{shopId}/offshelves")
    @ResponseBody
    public Object offshelfShop(@PathVariable Long shopId) {
        ReturnObject<VoObject> returnObject =  shopService.offshelfShop(shopId);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /* shop end */
}
