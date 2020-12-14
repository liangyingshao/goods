package cn.edu.xmu.activity.controller;

import cn.edu.xmu.activity.mapper.GrouponActivityPoMapper;
import cn.edu.xmu.activity.model.bo.ActivityStatus;
import cn.edu.xmu.activity.model.vo.GrouponVo;
import cn.edu.xmu.activity.service.GrouponService;
import cn.edu.xmu.ooad.annotation.Audit;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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
@RequestMapping(value = "/goods", produces = "application/json;charset=UTF-8")
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
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
    })
    @ApiResponse(code = 0,message = "成功")
    @Audit
    @GetMapping("/groupons/states")
    public Object getgrouponState() {
        ActivityStatus[] states= ActivityStatus.class.getEnumConstants();
        List<ActivityStatus> stateVos=new ArrayList<ActivityStatus>();
        for(int i=0;i<states.length;i++){
            stateVos.add(states[i]);
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
    @Audit
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
            ReturnObject<PageInfo<VoObject>> returnObject = grouponService.queryGroupons(shopId, spu_id, null, timeline, "", "", page, pagesize, false);
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
            ReturnObject<PageInfo<VoObject>> returnObject = grouponService.queryGroupons(id, spuid, state, null, beginTime, endTime, page, pagesize, true);
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
    public Object createGrouponofSPU(@PathVariable(name="id") Long id, @PathVariable(name="shopId") Long shopId, @RequestBody GrouponVo grouponVo ){

        //beginTime，endTime的验证
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime beginTime = null;
        try {
            beginTime = LocalDateTime.parse(grouponVo.getBeginTime(),dtf);
        } catch (Exception e) {
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
        }
        LocalDateTime endTime = null;
        try {
            endTime = LocalDateTime.parse(grouponVo.getEndTime(),dtf);
        } catch (Exception e) {
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
        }

        if(beginTime.isBefore(LocalDateTime.now()) ||
                endTime.isBefore(LocalDateTime.now())||
                endTime.isBefore(beginTime))
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);

        ReturnObject returnObject = grouponService.createGrouponofSPU(shopId,id,grouponVo);
        if (returnObject.getCode() == ResponseCode.OK) {
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
    public Object modifyGrouponofSPU(@PathVariable Long shopId, @PathVariable Long id,@RequestBody(required = true) GrouponVo grouponVo){

        //BeginTime，EndTime的验证
        LocalDateTime beginTime = null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            beginTime= LocalDateTime.parse(grouponVo.getBeginTime(),dtf);
        } catch (Exception e) {
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
        }
        LocalDateTime endTime = null;
        try {
            endTime = LocalDateTime.parse(grouponVo.getEndTime(),dtf);
        } catch (Exception e) {
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
        }
        if(endTime.isBefore(LocalDateTime.now())||
                beginTime.isBefore(LocalDateTime.now())||
                endTime.isBefore(beginTime))
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);

        ReturnObject returnObject = grouponService.modifyGrouponofSPU(shopId,id,grouponVo);
        return Common.getRetObject(returnObject);
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
        return Common.getRetObject(returnObject);
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
        return Common.getRetObject(returnObject);
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
        return Common.getRetObject(returnObject);
    }













}
