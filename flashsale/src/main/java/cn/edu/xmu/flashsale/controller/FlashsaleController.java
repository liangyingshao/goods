package cn.edu.xmu.flashsale.controller;

import cn.edu.xmu.flashsale.model.vo.FlashsaleItemRetVo;
import cn.edu.xmu.flashsale.model.vo.FlashsaleItemVo;
import cn.edu.xmu.flashsale.model.vo.FlashsaleModifVo;
import cn.edu.xmu.flashsale.model.vo.FlashsaleNewRetVo;
import cn.edu.xmu.flashsale.service.FlashsaleItemService;
import cn.edu.xmu.flashsale.service.FlashsaleService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.other.service.ITimeService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author: 24320182203259 邵良颖
 * Created at: 2020-12-09 11:33
 * version: 1.0
 */
@Api(value = "秒杀服务", tags = "flashsale")
@RestController /*Restful的Controller对象*/
//@RequestMapping(value = "/flashsale", produces = "application/json;charset=UTF-8")
@RequestMapping(produces = "application/json;charset=UTF-8")
public class FlashsaleController {
    private  static  final Logger logger = LoggerFactory.getLogger(FlashsaleController.class);

    @Autowired
    private FlashsaleService flashsaleService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private FlashsaleItemService flashsaleItemService;

    @DubboReference
    private ITimeService iTimeService;

    @ApiOperation(value = "flashsale001:查询某一时段秒杀活动详情",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", required = true, dataType="String", paramType="path")//时间段id
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/timesegments/{id}/flashsales")
    public Flux<FlashsaleItemRetVo> queryTopicsByTime(@PathVariable Long id) {
        return flashsaleItemService.queryTopicsByTime(id);
    }

    @ApiOperation(value = "flashsale003:获取当前时段秒杀列表,响应式API，会多次返回",  produces="application/json")
    @ApiImplicitParams({
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/flashsales/current")
    public Flux<FlashsaleItemRetVo> getCurrentflash() {
        Byte type = 1;
//        Long id = iTimeService.getCurrentSegmentId(type).getData();
        Long id = 9L;
        return flashsaleItemService.queryTopicsByTime(id);
    }

    @ApiOperation(value = "flashsale002:平台管理员在某个时段下新建秒杀",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Long", paramType="path"),//时间段id
            @ApiImplicitParam(name = "vo", required = true, dataType = "FlashsaleModifVo", paramType = "body")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PostMapping("/shops/{did}/timesegments/{id}/flashsales")
    public Object createflash(@PathVariable Long did,@PathVariable Long id, @Validated @RequestBody FlashsaleModifVo vo, BindingResult bindingResult)
    {
        Object binObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(null != binObject)
        {
            return binObject;
        }
        vo.setFlashDate(vo.getFlashDate().of(vo.getFlashDate().toLocalDate(), LocalTime.MIN));
        //falshDate不能小于等于明天
        if(!vo.getFlashDate().isAfter(LocalDateTime.now().plusDays(1))) {
            return new ReturnObject<>(ResponseCode.TOMORROW_FLASHSALE_INVALID);
        }
//        LocalDate date = LocalDate.now().plusDays(1); // get the tomorrow date
//        if(date.toString().compareTo(flashDate) > 0)//不允许增加明天之前的活动
//        {
//            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
//        }
//        //falshDate不小于明天
//        LocalDateTime flashDateParse = LocalDate.parse(flashDate,DateTimeFormatter.ISO_DATE).atStartOfDay();
        ReturnObject object = flashsaleService.createflash(id, vo.getFlashDate());
        if(object.getData()!=null)
        {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.decorateReturnObject(object);
        }
        else
        {
            return Common.getNullRetObj(new ReturnObject<>(object.getCode(), object.getErrmsg()), httpServletResponse);
        }
    }

    @ApiOperation(value = "flashsale004:平台管理员删除某个时段秒杀",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Long", paramType="path"),//秒杀id
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @DeleteMapping("/shops/{did}/flashsales/{id}")
    public Object deleteflashsale(@PathVariable Long id, @PathVariable Long did) {
//        ReturnObject object = flashsaleService.deleteflashsale(id);
//        return Common.decorateReturnObject(object);
        Byte state = 2;
        ReturnObject object = flashsaleService.updateFlashsaleState(id, state);//0：已下线，1：已上线，2：已删除
        return Common.decorateReturnObject(object);
    }

    @ApiOperation(value = "flashsale005:管理员修改秒杀活动",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "vo", required = true, dataType = "FlashsaleModifVo", paramType = "body")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PutMapping("/shops/{did}/flashsales/{id}")
    public Object updateflashsale(@PathVariable Long did, @PathVariable Long id, @Validated @RequestBody FlashsaleModifVo vo, BindingResult bindingResult)
    {
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(null != object)
        {
            return object;
        }
        vo.setFlashDate(vo.getFlashDate().of(LocalDate.now(), LocalTime.MIN));
        ReturnObject returnObject = flashsaleService.updateflashsale(id, vo.getFlashDate());
        return Common.decorateReturnObject(returnObject);
    }

    @ApiOperation(value = "flashsale006:平台管理员向秒杀活动添加商品SKU",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "vo", required = true, dataType = "FlashsaleItemVo", paramType = "body")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PostMapping("/shops/{did}/flashsales/{id}/flashitems")
    public Object addSKUofTopic(@PathVariable Long did, @PathVariable Long id, @Validated @RequestBody FlashsaleItemVo vo, BindingResult bindingResult) {
        logger.error("1");
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(null != object)
        {
            return object;
        }
        logger.error("2");
        ReturnObject returnObject = flashsaleItemService.addSKUofTopic(id, vo.getSkuId(), vo.getPrice(), vo.getQuantity());
        if(returnObject.getData()!=null) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.decorateReturnObject(returnObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(returnObject.getCode(), returnObject.getErrmsg()), httpServletResponse);
        }
    }

    @ApiOperation(value = "flashsale007:获取秒杀活动商品",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, dataType = "Long", paramType = "path"),//秒杀id
            @ApiImplicitParam(name="page", required = false, dataType = "int", paramType = "query", value="页码"),
            @ApiImplicitParam(name="pageSize", required = false, dataType = "int", paramType = "query", value="每页数目")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/flashsales/{id}/flashitems")
    public Object getSKUofTopic(@PathVariable Long id, @RequestParam(required = false, defaultValue = "1") Integer page, @RequestParam(required = false, defaultValue = "10") Integer pageSize)
    {
        ReturnObject<PageInfo<VoObject>> returnObject = flashsaleItemService.getSKUofTopic(id, page, pageSize);
        return Common.getPageRetObject(returnObject);
    }

    @ApiOperation(value = "flashsale008:平台管理员在秒杀活动删除商品SKU",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "fid", required = true, dataType = "Long", paramType = "path"),//秒杀id
            @ApiImplicitParam(name = "id", required = true, dataType = "Long", paramType = "path")//秒杀项id
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @DeleteMapping("/shops/{did}/flashsales/{fid}/flashitems/{id}")
    public Object deleteKUofTopic(@PathVariable Long did, @PathVariable Long fid, @PathVariable Long id) {
        ReturnObject object = flashsaleItemService.deleteSKUofTopic(fid, id);
        return Common.decorateReturnObject(object);
    }

    @ApiOperation(value = "flashsale009:管理员上线秒杀活动",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "did", required = true, dataType = "Long", paramType = "path"),//店铺id
            @ApiImplicitParam(name = "id", required = true, dataType = "Long", paramType = "path")//秒杀id
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PutMapping("/shops/{did}/flashsales/{id}/onshelves")
    public Object flashsaleOn(@PathVariable Long did, @PathVariable Long id) {
        Byte state = 1;
        ReturnObject object = flashsaleService.updateFlashsaleState(id, state);//0：已下线，1：已上线，2：已删除
        return Common.decorateReturnObject(object);
    }

    @ApiOperation(value = "flashsale0010:管理员下线秒杀活动",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "did", required = true, dataType = "Long", paramType = "path"),//店铺id
            @ApiImplicitParam(name = "id", required = true, dataType = "Long", paramType = "path")//秒杀id
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PutMapping("/shops/{did}/flashsales/{id}/offshelves")
    public Object flashsaleOff(@PathVariable Long did, @PathVariable Long id) {
        Byte state = 0;
        ReturnObject object = flashsaleService.updateFlashsaleState(id, state);//0：已下线，1：已上线，2：已删除
        return Common.decorateReturnObject(object);
    }
}
