package cn.edu.xmu.activity.controller;

import cn.edu.xmu.activity.model.bo.ActivityStatus;
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
@RequestMapping(value = "/goods", produces = "application/json;charset=UTF-8")
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
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
    })
    @ApiResponse(code = 0,message = "成功")
    @Audit
    @GetMapping("/presales/states")
    public Object getPresaleState() {
        ActivityStatus[] states= ActivityStatus.class.getEnumConstants();
        List<ActivityStatus> stateVos = new ArrayList<ActivityStatus>();
        for(int i=0;i<states.length;i++){
            stateVos.add(states[i]);
        }
        return ResponseUtil.ok(new ReturnObject<List>(stateVos).getData());
    }

    /**
     * description: Presale002 * customerQueryPresales
     * version: 1.0
     * date: 2020/12/14 9:25
     * author: 杨铭
     *
     * @param timeline 时间：0 还未开始的， 1 明天开始的，2 正在进行中的，3 已经结束的
     * @param skuId skuId
     * @param shopId 店铺id
     * @param page 页码数
     * @param pagesize 每页条数
     * @return java.lang.Object
     */
    @ApiOperation("顾客查询所有有效预售活动")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @ResponseBody
    @GetMapping("/presales")
    public Object customerQueryPresales(
            @RequestParam(required = false) int timeline,
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


    /**
     * description: Presale003 * adminQueryPresales
     * version: 1.0
     * date: 2020/12/14 9:27
     * author: 杨铭
     *
     * @param shopId 店铺id
     * @param state 状态
     * @param skuId skuId
     * @param page 页码数
     * @param pagesize 每页条数
     * @return java.lang.Object
     */
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
    @GetMapping("/shops/{id}/presales")
    public Object adminQueryPresales(
            @PathVariable Long shopId,
            @RequestParam(required = false) Integer state,
            @RequestParam(required = false) Long skuId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pagesize
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



    /**
     * description: Presale004 * createPresaleOfSKU
     * version: 1.0
     * date: 2020/12/14 9:28
     * author: 杨铭
     *
     * @param id skuId
     * @param shopId 店铺id
     * @param presaleVo 预售新增/修改vo
     * @return java.lang.Object
     */
    @ApiOperation(value="管理员新增SkU预售活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value ="用户token", paramType = "header", dataType = "String",  required = true),
            @ApiImplicitParam(name = "shopId", value ="商铺id", paramType = "path", dataType = "Integer",  required = true),
            @ApiImplicitParam(name = "id", value ="商品SPUid", paramType = "path", dataType = "Integer",  required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PostMapping("/shops/{shopid}/skus/{id}/presales")
    @ResponseBody
    public Object createPresaleOfSKU(@PathVariable(name = "id") Long id, @PathVariable(name="shopId") Long shopId, @RequestBody PresaleVo presaleVo ){

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(LocalDateTime.parse(presaleVo.getBeginTime(),dtf).isBefore(LocalDateTime.now())||
                LocalDateTime.parse(presaleVo.getEndTime(),dtf).isBefore(LocalDateTime.now())||
                LocalDateTime.parse(presaleVo.getEndTime(),dtf).isBefore(LocalDateTime.parse(presaleVo.getBeginTime(),dtf)))
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);

        ReturnObject returnObject = null;
        try {
            returnObject = presaleService.createPresaleOfSKU(shopId,id,presaleVo);
        } catch (Exception e) {
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
        }
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }


    /**
     * description: Presale005 modifyPresaleofSKU
     * version: 1.0
     * date: 2020/12/14 9:28
     * author: 杨铭
     *
     * @param shopId 店铺id
     * @param id skuId
     * @param presaleVo 预售新增/修改对象
     * @return java.lang.Object
     */
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
    public Object modifyPresaleofSKU(@PathVariable Long shopId, @PathVariable Long id, @RequestBody(required = true) PresaleVo presaleVo){

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(LocalDateTime.parse(presaleVo.getBeginTime(),dtf).isBefore(LocalDateTime.now())||
                LocalDateTime.parse(presaleVo.getEndTime(),dtf).isBefore(LocalDateTime.now())||
                LocalDateTime.parse(presaleVo.getEndTime(),dtf).isBefore(LocalDateTime.parse(presaleVo.getBeginTime(),dtf)))
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);

        ReturnObject returnObject = presaleService.modifyPresaleOfSKU(shopId,id,presaleVo);
        return Common.getRetObject(returnObject);
    }


    /**
     * description: Presale006 cancelPresaleOfSKU
     * version: 1.0
     * date: 2020/12/14 9:30
     * author: 杨铭
     *
     * @param shopId 店铺id
     * @param id skuId
     * @return java.lang.Object
     */
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


    /**
     * description: Presale007 putPresaleOnShelves
     * version: 1.0
     * date: 2020/12/14 9:31
     * author: 杨铭
     *
     * @param id 预售id
     * @param shopId 店铺id
     * @return java.lang.Object
     */
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


    /**
     * description: Presale008 putPresaleOffShelves
     * version: 1.0
     * date: 2020/12/14 9:32
     * author: 杨铭
     *
     * @param id 预售id
     * @param shopId 店铺id
     * @return java.lang.Object
     */
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
