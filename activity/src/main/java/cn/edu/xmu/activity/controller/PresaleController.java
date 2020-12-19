package cn.edu.xmu.activity.controller;

import cn.edu.xmu.activity.model.bo.ActivityStatus;
import cn.edu.xmu.activity.model.bo.Presale;
import cn.edu.xmu.activity.model.vo.ActivityStatusRetVo;
import cn.edu.xmu.activity.model.vo.PresaleVo;
import cn.edu.xmu.activity.service.PresaleService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
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
import java.time.LocalDateTime;
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
@RequestMapping(value = "/goods", produces = "application/json;charset=UTF-8")
//@RequestMapping(produces = "application/json;charset=UTF-8")
public class PresaleController {

    @Autowired
    private PresaleService presaleService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    private static final Logger logger = LoggerFactory.getLogger(PresaleController.class);
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
            ReturnObject<PageInfo<VoObject>> returnObject = presaleService.CustomerQueryPresales(shopId, skuId, null, timeline, page, pagesize, false);
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
            ReturnObject<List<Presale>> returnObject = presaleService.AdminQueryPresales(shopId, skuId, state, null);
            object = Common.decorateReturnObject(returnObject);
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
    public Object createPresaleOfSKU(@PathVariable(name = "id") Long id, @PathVariable(name="shopId") Long shopId, @Validated @NotNull @RequestBody PresaleVo presaleVo, BindingResult bindingResult){

        Object retObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != retObject) {
            logger.debug("validate fail");
            return retObject;
        }

        //不能为空
        if(presaleVo.getEndTime()==null || presaleVo.getBeginTime()==null ||presaleVo.getPayTime() == null){
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.FIELD_NOTVALID));
        }
        //endtime<now,begin<now
        if(presaleVo.getEndTime().isBefore(LocalDateTime.now())||presaleVo.getBeginTime().isBefore(LocalDateTime.now())){
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.FIELD_NOTVALID));
        }
        //begintime>endtime
        if(presaleVo.getEndTime().isBefore(LocalDateTime.now())||
                presaleVo.getEndTime().isBefore(presaleVo.getPayTime())||
                presaleVo.getPayTime().isBefore(presaleVo.getBeginTime())){
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.FIELD_NOTVALID));
        }


        ReturnObject returnObject = null;

        returnObject = presaleService.createPresaleOfSKU(shopId,id,presaleVo);

        if (returnObject.getCode() == ResponseCode.OK) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
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
    public Object modifyPresaleofSKU(@PathVariable Long shopId, @Depart Long departId, @PathVariable Long id, @Validated @NotNull @RequestBody(required = true) PresaleVo presaleVo, BindingResult bindingResult){

        Object retObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != retObject) {
            logger.debug("validate fail");
            return retObject;
        }

        //paytime<begintime
        if(presaleVo.getBeginTime()!=null &&
                presaleVo.getPayTime() != null &&
                presaleVo.getPayTime().isBefore(presaleVo.getBeginTime())){
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.FIELD_NOTVALID));
        }

        //paytime>endtime
        if(presaleVo.getEndTime()!=null && presaleVo.getPayTime() != null && presaleVo.getPayTime().isAfter(presaleVo.getEndTime())){
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.FIELD_NOTVALID));
        }

        //begintime>endtime
        if(presaleVo.getBeginTime()!=null && presaleVo.getEndTime() != null && presaleVo.getBeginTime().isAfter(presaleVo.getEndTime())){
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.FIELD_NOTVALID));
        }

        //endtime<now
        if(presaleVo.getEndTime()!=null && presaleVo.getEndTime().isBefore(LocalDateTime.now())){
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.FIELD_NOTVALID));
        }
        //begintime<now
        if(presaleVo.getBeginTime()!=null && presaleVo.getBeginTime().isBefore(LocalDateTime.now())){
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.FIELD_NOTVALID));
        }
        //paytime<now
        if(presaleVo.getPayTime()!=null && presaleVo.getPayTime().isBefore(LocalDateTime.now())){
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.FIELD_NOTVALID));
        }

        if(shopId!=departId && departId!=0L)
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));

        ReturnObject returnObject = presaleService.modifyPresaleOfSKU(shopId,id,presaleVo);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
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
    public Object cancelPresaleOfSKU(@PathVariable Long shopId, @Depart Long departId, @PathVariable Long id) {
        if(shopId!=departId && departId!=0L)
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
        ReturnObject returnObject =  presaleService.cancelPresaleOfSKU(shopId, id);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
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
    public Object putPresaleOnShelves(@PathVariable Long id, @Depart Long departId, @PathVariable Long shopId){
        if(shopId!=departId && departId!=0L)
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
        ReturnObject returnObject = presaleService.putPresaleOnShelves(shopId,id);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
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
    public Object putPresaleOffShelves(@PathVariable Long id, @Depart Long departId, @PathVariable Long shopId){

        if(shopId!=departId && departId!=0L)
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));

        ReturnObject returnObject = presaleService.putPresaleOffShelves(shopId,id);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }


}
