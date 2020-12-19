package cn.edu.xmu.activity.controller;

import cn.edu.xmu.activity.mapper.GrouponActivityPoMapper;
import cn.edu.xmu.activity.model.bo.ActivityStatus;
import cn.edu.xmu.activity.model.vo.ActivityStatusRetVo;
import cn.edu.xmu.activity.model.vo.GrouponVo;
import cn.edu.xmu.activity.service.GrouponService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.lang.invoke.LambdaConversionException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * description: GrouponController
 * date: 2020/12/10 14:40
 * author: 杨铭
 * version: 1.0
 */
@Api(value = "团购服务", tags = "Groupon")
@RestController /*Restful的Controller对象*/
//@RequestMapping(value = "/goods", produces = "application/json;charset=UTF-8")
@RequestMapping(produces = "application/json;charset=UTF-8")
public class GrouponController {

    @Autowired
    GrouponService grouponService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    private static final Logger logger = LoggerFactory.getLogger(GrouponController.class);


    /**
     * description: Groupon001 获得团购活动的所有状态
     * version: 1.0
     * date: 2020/12/3 18:12
     * author: 杨铭
     *
     * @return java.lang.Object
     */
    @ApiOperation(value="获得团购活动的所有状态")
    @ApiResponse(code = 0,message = "成功")
    @GetMapping("/groupons/states")
    public Object getgrouponState() {
        ActivityStatus[] states= ActivityStatus.class.getEnumConstants();
        List<ActivityStatusRetVo> stateVos=new ArrayList<ActivityStatusRetVo>();
        for(int i=0;i<states.length;i++){
            stateVos.add(new ActivityStatusRetVo(states[i]));
        }
        return ResponseUtil.ok(new ReturnObject<List>(stateVos).getData());
    }



    /**
     * description: Groupon002 顾客查询团购
     * version: 1.0
     * date: 2020/12/7 23:27
     * author: 杨铭
     *
     * @param timeline 时间：0 还未开始的， 1 明天开始的，2 正在进行中的，3 已经结束的
     * @param spu_id spuid
     * @param shopId shopid
     * @param page 页码
     * @param pagesize 每页数目
     * @return java.lang.Object
     */
    @ApiOperation("顾客查询所有团购活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value ="用户token", paramType = "header", dataType = "String",  required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @ResponseBody
    @GetMapping("/groupons")
    public Object customerQueryGroupons(
            @RequestParam(required = false) Integer timeline,
            @RequestParam(required = false) Long spu_id,
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pagesize){


        Object object = null;

        if(page <= 0 || pagesize <= 0) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        } else {
            ReturnObject<PageInfo<VoObject>> returnObject = grouponService.queryGroupons(shopId, spu_id, null, timeline, null, null, page, pagesize, false);
            object = Common.getPageRetObject(returnObject);
        }

        return object;
    }



    /**
     * description: Groupon003 管理员查询团购
     * version: 1.0
     * date: 2020/12/11 11:06
     * author: 杨铭
     *
     * @param id 店铺id
     * @param state 团购状态：1：未上线，2：已上线，3：已删除
     * @param spuid spuid
     * @param beginTime 起始时间
     * @param endTime 结束时间
     * @param page 页码数
     * @param pagesize 页数
     * @return java.lang.Object
     */
    @ApiOperation("管理员查询所有团购(包括下线的)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value ="用户token", paramType = "header", dataType = "String",  required = true),
            @ApiImplicitParam(name = "id", value ="店铺id", paramType = "path", dataType = "Integer",  required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @ResponseBody
    @GetMapping("/shops/{id}/groupons")
    public Object adminQueryGroupons(
            @PathVariable Long id,
            @RequestParam(required = false) Integer state,
            @RequestParam(required = false) Long spuid,
            @RequestParam(required = false) String beginTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pagesize
    ){

        Object object = null;
        if(page <= 0 || pagesize <= 0) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        } else {
            //时间处理

            LocalDateTime bt = null;
            if(beginTime!=null){
                try {
                    bt = LocalDateTime.parse(beginTime,DateTimeFormatter.ISO_DATE_TIME);
                } catch (Exception e) {
                    object = Common.decorateReturnObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID));
                }
            }

            LocalDateTime et = null;
            if(endTime!=null){
                try {
                    et = LocalDateTime.parse(endTime,DateTimeFormatter.ISO_DATE_TIME);
                } catch (Exception e) {
                    object = Common.decorateReturnObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID));
                }
            }

            ReturnObject<PageInfo<VoObject>> returnObject = grouponService.queryGroupons(id, spuid, state, null, bt, et, page, pagesize, true);
            object = Common.getPageRetObject(returnObject);
        }

        return object;
    }



    /**
     * description: Groupon004 * 新增团购活动
     * version: 1.0
     * date: 2020/12/6 20:39
     * author: 杨铭
     *
     * @param id spuId
     * @param shopId 店铺id
     * @param grouponVo 新增、修改vo
     * @return java.lang.Object
     */
    @ApiOperation(value="管理员对SPU新增团购活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value ="用户token", paramType = "header", dataType = "String",  required = true),
            @ApiImplicitParam(name = "shopId", value ="商铺id", paramType = "path", dataType = "Integer",  required = true),
            @ApiImplicitParam(name = "id", value ="商品SPUid", paramType = "path", dataType = "Integer",  required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PostMapping("/shops/{shopId}/spus/{id}/groupons")
    @ResponseBody
    public Object createGrouponofSPU(@PathVariable(name="id") Long id, @Depart @PathVariable(name="shopId") Long shopId, @Validated @RequestBody(required = true) GrouponVo grouponVo, BindingResult bindingResult){

        Object retObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != retObject) {
            logger.debug("validate fail");
            return retObject;
        }

        //beginTime，endTime不能空
        if(grouponVo.getBeginTime()==null || grouponVo.getEndTime() == null){
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.FIELD_NOTVALID));
        }
        if(grouponVo.getEndTime().isBefore(LocalDateTime.now())||
                grouponVo.getBeginTime().isAfter(grouponVo.getEndTime())) {
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.FIELD_NOTVALID));
        }

        ReturnObject returnObject = grouponService.createGrouponofSPU(shopId,id,grouponVo);
        if (returnObject.getCode() == ResponseCode.OK) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }



    /**
     * description: Groupon005 修改团购活动
     * version: 1.0
     * date: 2020/12/6 23:11
     * author: 杨铭
     *
     * @param shopId 店铺id
     * @param id 团购活动id
     * @param grouponVo 修改vo
     * @return java.lang.Object
     */
    @ApiOperation(value="管理员修改SPU团购活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value ="用户token", paramType = "header", dataType = "String",  required = true),
            @ApiImplicitParam(name = "shopId", value ="商铺id", paramType = "path", dataType = "Integer",  required = true),
            @ApiImplicitParam(name = "id", value ="团购活动id", paramType = "path", dataType = "Integer",  required = true),
            @ApiImplicitParam(name = "grouponVo", value ="团购活动", paramType = "body", dataType = "GrouponVo",  required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 907, message = "团购活动状态禁止"),
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @ResponseBody
    @PutMapping("/shops/{shopId}/groupons/{id}")
    public Object modifyGrouponofSPU(@PathVariable Long shopId, @PathVariable Long id,@Validated @RequestBody(required = true) GrouponVo grouponVo,BindingResult bindingResult){

        Object retObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != retObject) {
            logger.debug("validate fail");
            return retObject;
        }

        //BeginTime，EndTime的验证
        if(grouponVo.getEndTime()!=null && grouponVo.getEndTime().isBefore(LocalDateTime.now())){
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.FIELD_NOTVALID));
        }
        if(grouponVo.getEndTime()!=null && grouponVo.getBeginTime()!=null && grouponVo.getEndTime().isBefore(grouponVo.getBeginTime())){
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.FIELD_NOTVALID));
        }

        ReturnObject returnObject = grouponService.modifyGrouponofSPU(shopId,id,grouponVo);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }



    /**
     * description: Groupon006 删除团购活动
     * version: 1.0
     * date: 2020/12/7 19:33
     * author: 杨铭
     *
     * @param id 团购id
     * @return java.lang.Object
     */
    @ApiOperation(value="管理员下架SPU团购活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value ="用户token", paramType = "header", dataType = "String",  required = true),
            @ApiImplicitParam(name = "shopId", value ="商铺id", paramType = "path", dataType = "Integer",  required = true),
            @ApiImplicitParam(name = "id", value ="团购活动id", paramType = "path", dataType = "Integer",  required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 907, message = "团购活动状态禁止"),
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @DeleteMapping("/shops/{shopId}/groupons/{id}")
    public Object cancelGrouponofSPU(@PathVariable Long shopId, @PathVariable Long id) {
        ReturnObject returnObject =  grouponService.cancelGrouponofSPU(shopId,id);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }



    /**
     * description: Groupon007 上架团购活动
     * version: 1.0
     * date: 2020/12/11 11:24
     * author: 杨铭
     *
     * @param id 团购id
     * @param shopId 店铺id
     * @return java.lang.Object
     */
    @ApiOperation(value="管理员上架spu团购活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value ="用户token", paramType = "header", dataType = "String",  required = true),
            @ApiImplicitParam(name = "shopId", value ="商铺id", paramType = "path", dataType = "Integer",  required = true),
            @ApiImplicitParam(name = "id", value ="团购活动id", paramType = "path", dataType = "Integer",  required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 907, message = "团购活动状态禁止"),
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @ResponseBody
    @PutMapping("/shops/{shopId}/groupons/{id}/onshelves")
    public Object putGrouponOnShelves(@PathVariable Long id,@PathVariable Long shopId){

        ReturnObject returnObject = grouponService.putGrouponOnShelves(shopId,id);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }



    /**
     * description: Groupon008 下架团购活动
     * version: 1.0
     * date: 2020/12/11 11:24
     * author: 杨铭
     *
     * @param id 团购id
     * @param shopId 店铺id
     * @return java.lang.Object
     */
    @ApiOperation(value="管理员下架spu团购活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value ="用户token", paramType = "header", dataType = "String",  required = true),
            @ApiImplicitParam(name = "shopId", value ="商铺id", paramType = "path", dataType = "Integer",  required = true),
            @ApiImplicitParam(name = "id", value ="团购活动id", paramType = "path", dataType = "Integer",  required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 907, message = "团购活动状态禁止"),
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @ResponseBody
    @PutMapping("/shops/{shopId}/groupons/{id}/offshelves")
    public Object putGrouponOffShelves(@PathVariable Long id,@PathVariable Long shopId){

        ReturnObject returnObject = grouponService.putGrouponOffShelves(shopId,id);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }













}
