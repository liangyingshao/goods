package cn.edu.xmu.activity.controller;

import cn.edu.xmu.activity.model.bo.ActivityStatus;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    /**
     * description: getPresaleState
     * version: 1.0
     * date: 2020/12/11 11:16
     * author: 杨铭
     *
     * @param
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





}
