package cn.edu.xmu.activity.controller;

import cn.edu.xmu.activity.model.bo.ActivityStatus;
import cn.edu.xmu.activity.model.vo.ActivityStatusRetVo;
import cn.edu.xmu.activity.model.vo.PresaleVo;
import cn.edu.xmu.activity.service.PresaleService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * description: PresaleController
 * date: 2020/12/11 11:14
 * author: 杨铭
 * version: 1.0
 */
@Api(value = "预售服务", tags = "Presale")
@RestController /*Restful的Controller对象*/
//@RequestMapping(value = "/goods", produces = "application/json;charset=UTF-8")
@RequestMapping(produces = "application/json;charset=UTF-8")
public class PresaleController {

    @Autowired
    private PresaleService presaleService;

    @Autowired
    private HttpServletResponse httpServletResponse;


    /**
     * description: Presale001 getPresaleState
     * version: 1.0
     * date: 2020/12/11 11:16
     * author: 杨铭
     *
     * @return java.lang.Object
     */
    @ApiOperation(value="获得预售活动的所有状态")
    @ApiResponse(code = 0,message = "成功")
    @GetMapping("/presales/states")
    public Object getPresaleState() {
        ActivityStatus[] states= ActivityStatus.class.getEnumConstants();
        List<ActivityStatusRetVo> stateVos=new ArrayList<ActivityStatusRetVo>();
        for(int i=0;i<states.length;i++){
            stateVos.add(new ActivityStatusRetVo(states[i]));
        }
        return ResponseUtil.ok(new ReturnObject<List>(stateVos).getData());
    }


    @ApiOperation("顾客查询所有有效预售活动")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @ResponseBody
    @GetMapping("/presales")
    public Object customerQueryPresales(
            @RequestParam(required = false) Integer timeline,
            @RequestParam(required = false) Long skuId,
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pagesize){


        Object object = null;

        if(page <= 0 || pagesize <= 0) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        } else {
            ReturnObject<PageInfo<VoObject>> returnObject = presaleService.QueryPresales(shopId, skuId, null, timeline, page, pagesize, false);
            object = Common.getPageRetObject(returnObject);
        }

        return object;
    }


    @ApiOperation("管理员查询所有预售活动(包括下线的)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value ="用户token", paramType = "header", dataType = "String",  required = true),
            @ApiImplicitParam(name = "id", value ="店铺id", paramType = "path", dataType = "Integer",  required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @ResponseBody
    @GetMapping("/shops/{shopId}/presales")
    public Object adminQueryPresales(
            @PathVariable Long shopId,
            @RequestParam(required = false) Integer state,
            @RequestParam(required = false) Long skuId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pagesize
    ){

        Object object = null;

        if(page <= 0 || pagesize <= 0) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        } else {
            ReturnObject<PageInfo<VoObject>> returnObject = presaleService.QueryPresales(shopId, skuId, state, null, page, pagesize,true);
            object = Common.getPageRetObject(returnObject);
        }

        return object;
    }




    @ApiOperation(value="管理员新增SKU预售活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value ="用户token", paramType = "header", dataType = "String",  required = true),
            @ApiImplicitParam(name = "shopId", value ="商铺id", paramType = "path", dataType = "Integer",  required = true),
            @ApiImplicitParam(name = "id", value ="商品SKUid", paramType = "path", dataType = "Integer",  required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PostMapping("/shops/{shopId}/skus/{id}/presales")
    @ResponseBody
    public Object createPresaleOfSKU(@PathVariable(name = "id") Long id, @PathVariable(name="shopId") Long shopId, @RequestBody PresaleVo presaleVo ){

        //beginTime，endTime的验证
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime beginTime = null;
        try {
            beginTime = LocalDateTime.parse(presaleVo.getBeginTime(),dtf);
        } catch (Exception e) {
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        }
        LocalDateTime endTime = null;
        try {
            endTime = LocalDateTime.parse(presaleVo.getEndTime(),dtf);
        } catch (Exception e) {
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        }
        LocalDateTime payTime = null;
        try {
            payTime = LocalDateTime.parse(presaleVo.getPayTime(),dtf);
        } catch (Exception e) {
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        }

        if(beginTime.isBefore(LocalDateTime.now())||
                endTime.isBefore(LocalDateTime.now())||
                endTime.isBefore(payTime)||
                payTime.isBefore(beginTime))
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);

        ReturnObject returnObject = null;

        returnObject = presaleService.createPresaleOfSKU(shopId,id,presaleVo);

        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }



    @ApiOperation(value="管理员修改Sku预售活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value ="用户token", paramType = "header", dataType = "String",  required = true),
            @ApiImplicitParam(name = "shopId", value ="商铺id", paramType = "path", dataType = "Integer",  required = true),
            @ApiImplicitParam(name = "id", value ="预售活动id", paramType = "path", dataType = "Integer",  required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 906, message = "预售活动状态禁止"),
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PutMapping("/shops/{shopId}/presales/{id}")
    public Object modifyPresaleofSKU(@PathVariable Long shopId, @PathVariable Long id,@RequestBody(required = true) PresaleVo presaleVo){

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(LocalDateTime.parse(presaleVo.getBeginTime(),dtf).isBefore(LocalDateTime.now())||
                LocalDateTime.parse(presaleVo.getEndTime(),dtf).isBefore(LocalDateTime.now())||
                LocalDateTime.parse(presaleVo.getEndTime(),dtf).isBefore(LocalDateTime.parse(presaleVo.getBeginTime(),dtf)))
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);

        ReturnObject returnObject = presaleService.modifyPresaleOfSKU(shopId,id,presaleVo);
        return Common.getRetObject(returnObject);
    }


    @ApiOperation(value="管理员逻辑删除sku预售活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value ="用户token", paramType = "header", dataType = "String",  required = true),
            @ApiImplicitParam(name = "shopId", value ="商铺id", paramType = "path", dataType = "Integer",  required = true),
            @ApiImplicitParam(name = "id", value ="预售活动id", paramType = "path", dataType = "Integer",  required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 906, message = "预售活动状态禁止"),
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @DeleteMapping("/shops/{shopId}/presales/{id}")
    public Object cancelPresaleOfSKU(@PathVariable Long shopId, @PathVariable Long id) {
        ReturnObject returnObject =  presaleService.cancelPresaleOfSKU(shopId, id);
        return Common.getRetObject(returnObject);
    }


    @ApiOperation(value="管理员上架sku预售活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value ="用户token", paramType = "header", dataType = "String",  required = true),
            @ApiImplicitParam(name = "shopId", value ="商铺id", paramType = "path", dataType = "Integer",  required = true),
            @ApiImplicitParam(name = "id", value ="预售活动id", paramType = "path", dataType = "Integer",  required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 906, message = "预售活动状态禁止"),
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @ResponseBody
    @PutMapping("/shops/{shopId}/presales/{id}/onshelves")
    public Object putPresaleOnShelves(@PathVariable Long id,@PathVariable Long shopId){

        ReturnObject returnObject = presaleService.putPresaleOnShelves(shopId,id);
        return Common.getRetObject(returnObject);
    }

    @ApiOperation(value="管理员上架sku预售活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value ="用户token", paramType = "header", dataType = "String",  required = true),
            @ApiImplicitParam(name = "shopId", value ="商铺id", paramType = "path", dataType = "Integer",  required = true),
            @ApiImplicitParam(name = "id", value ="预售活动id", paramType = "path", dataType = "Integer",  required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 906, message = "预售活动状态禁止"),
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @ResponseBody
    @PutMapping("/shops/{shopId}/presales/{id}/offshelves")
    public Object putPresaleOffShelves(@PathVariable Long id,@PathVariable Long shopId){

        ReturnObject returnObject = presaleService.putPresaleOffShelves(shopId,id);
        return Common.getRetObject(returnObject);
    }


}
