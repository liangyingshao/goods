package cn.edu.xmu.flashsale.controller;

import cn.edu.xmu.flashsale.service.FlashsaleService;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author: 24320182203259 邵良颖
 * Created at: 2020-12-09 11:33
 * version: 1.0
 */
@Api(value = "秒杀服务", tags = "flashsale")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/flashsale", produces = "application/json;charset=UTF-8")
public class FlashsaleController {
    private  static  final Logger logger = LoggerFactory.getLogger(FlashsaleController.class);

    @Autowired
    private FlashsaleService flashsaleService;

    @ApiOperation(value = "flashsale001:查询某一时段秒杀活动详情",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", required = true, dataType="String", paramType="path")//时间段id
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/timesegments/{id}/flashsales")
    public Object queryTopicsByTime(@PathVariable Long id) {
        ReturnObject object = flashsaleService.queryTopicsByTime(id);
        return Common.decorateReturnObject(object);
    }

    @ApiOperation(value = "flashsale002:平台管理员在某个时段下新建秒杀",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="String", paramType="path"),//时间段id
            @ApiImplicitParam(name = "flashDate",required = true, dataType = "String", paramType = "query", value = "秒杀活动日期")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PostMapping("/timesegments/{id}/flashsales")
    public Object createflash(@PathVariable Long id, @RequestParam(required = true) String flashDate) {
        //falshDate不能小于当前日期，先获取当前日期转化为字符串，与flashDate相比较，flashDate不能小于当前字符串
//    public Object createflash(@PathVariable Long id) {
        LocalDate date = LocalDate.now(); // get the current date
//        String flashDate = "2021-01-07";
        if(date.toString().compareTo(flashDate) > 0)
        {
            return new ReturnObject<>(ResponseCode.TIMESEG_CONFLICT);
        }
        //falshDate不小于当前日期
        LocalDateTime flashDateParse = LocalDate.parse(flashDate,DateTimeFormatter.ISO_DATE).atStartOfDay();
        ReturnObject object = flashsaleService.createflash(id, flashDateParse);
        return Common.decorateReturnObject(object);
    }
}
