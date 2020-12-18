package cn.edu.xmu.goods.controller;

import cn.edu.xmu.goods.model.bo.Comment;
import cn.edu.xmu.goods.model.vo.*;
import cn.edu.xmu.goods.service.CommentService;
import cn.edu.xmu.goods.service.FloatPriceService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.annotation.LoginUser;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 业务: 评论模块控制器
 * @return
 * @author: 24320182203259 邵良颖
 * Created at: 2020-12-01 21:24
 * version: 1.0
 */
@Api(value = "评论服务", tags = "comment")
@RestController /*Restful的Controller对象*/
//@RequestMapping(value = "/goods", produces = "application/json;charset=UTF-8")
@RequestMapping(produces = "application/json;charset=UTF-8")
public class CommentController {

    private  static  final Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    CommentService commentService;

    @Autowired
    FloatPriceService floatPriceService;

    @Autowired
    HttpServletResponse httpServletResponse;

    /**
     * 业务: comment001:获得评论的所有状态
     * @param
     * @return java.lang.Object
     * @author: 24320182203259 邵良颖
     * Created at: 2020-12-01 15:55
     * version: 1.0
     */
    @ApiOperation(value = "comment001:获得评论的所有状态",  produces="application/json")
    @ApiImplicitParams({
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/comments/states")
    public Object getcommentState() {
        Comment.State[] states=Comment.State.class.getEnumConstants();
        List<CommentStateRetVo> stateVos=new ArrayList<CommentStateRetVo>();
        for(int i=0;i<states.length;i++){
            stateVos.add(new CommentStateRetVo(states[i]));
        }
        return ResponseUtil.ok(new ReturnObject<List>(stateVos).getData());
    }

    /**
     * 业务: comment002买家新增sku的评论
     * @param id 订单明细的id
     * @param vo
     * @param userId
     * @return java.lang.Object
     * @author: 24320182203259 邵良颖
     * Created at: 2020-12-01 15:55
     * version: 1.0
     */
    @ApiOperation(value = "买家新增sku的评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="String", paramType="path"),//订单明细的id
            @ApiImplicitParam(paramType = "body",dataType = "CommentVo",name = "vo",value = "可修改的信息",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 903, message = "用户没有购买此商品"),
            @ApiResponse(code = 941, message = "该订单条目已评论")
    })
    @Audit
    @PostMapping("/orderitems/{id}/comments")
    @ResponseBody
    public Object addSkuComment(@PathVariable Long id,  @LoginUser @ApiIgnore @RequestParam(required = false) Long userId, @Validated @RequestBody CommentVo vo,BindingResult bindingResult){
//        logger.error("11111111111111");
        Object object = Common.processFieldErrors(bindingResult,httpServletResponse);
        if(null!=object){
            return object;
        }
//        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
//        if (null != returnObject) {
//            logger.error("return:"+returnObject.toString());
//            return returnObject;
//        }
        Comment comment = vo.createComment();
        comment.setType(vo.getType());
        comment.setContent(vo.getContent());
        comment.setOrderitemId(id);
        comment.setCustomerId(userId);
        ReturnObject<CommentRetVo> commentRetVoReturnObject = commentService.addSkuComment(comment);
        return Common.decorateReturnObject(commentRetVoReturnObject);
    }

    /**
     * 业务: comment003：查询已通过审核的评论
     * @param id sku的id
     * @param page 页数
     * @param pageSize 页大小
     * @return java.lang.Object
     * @author: 24320182203259 邵良颖
     * Created at: 2020-12-01 15:54
     * version: 1.0
     */
    @ApiOperation(value = "查询已通过审核的评论", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "SKU Id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/skus/{id}/comments")
    public Object selectAllPassComment(
            @PathVariable("id") Long id,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        ReturnObject<PageInfo<VoObject>> returnObject =  commentService.selectAllPassComment(id, page, pageSize);
        return Common.getPageRetObject(returnObject);
    }

    /**
     * 业务: comment004 管理员审核评论
     * @param id
     * @param conclusion
     * @param shopid
     * @return java.lang.Object
     * @author: 24320182203259 邵良颖
     * Created at: 2020-12-01 21:13
     * version: 1.0
     */
    @ApiOperation(value = "管理员审核评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path"),
            @ApiImplicitParam(name="conclusion", required = true, dataType="Boolean", paramType="body")

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作的资源id不存在"),
            @ApiResponse(code = 705, message = "无权限访问")
    })
    @Audit // 需要认证
    @PutMapping("/shops/{did}/comments/{id}/confirm")
    public Object auditComment(@PathVariable("did") Long did, @PathVariable("id") Long id, @RequestBody CommentAuditVo conclusion,@Depart Long shopid) {
        ReturnObject returnObject=null;
//        if(shopid==0)
//        {
        returnObject=commentService.auditComment(id, conclusion.getConclusion());
//        }
//        else
//        {
//            return new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW);
//        }
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 业务: comment005买家查看自己的评价记录
     * @param id
     * @param page
     * @param pageSize
     * @return java.lang.Object
     * @author: 24320182203259 邵良颖
     * Created at: 2020-12-01 21:37
     * version: 1.0
     */
    @ApiOperation(value = "买家查看自己的评价记录", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("/comments")
    public Object showComment(
            @LoginUser Long id,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        ReturnObject<PageInfo<VoObject>> returnObject =  commentService.showComment(id, page, pageSize);
        return Common.getPageRetObject(returnObject);
    }

    //校验token里的部门id和参数里的店铺id是否一致
    //校验部门id是不是0
    @ApiOperation(value = "管理员查看未审核/已审核评论列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path"),
            @ApiImplicitParam(name="conclusion", required = true, dataType="Boolean", paramType="body")

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作的资源id不存在"),
            @ApiResponse(code = 705, message = "无权限访问")
    })
    @Audit // 需要认证
    @GetMapping("/shops/{id}/comments/all")
    public Object showUnAuditComments(@PathVariable("id") Long id,//路径中的店铺id
                                      //@LoginUser Long user_id,
                                      @Depart Long departId,//token中的部门id
                                      @RequestParam(required = false, defaultValue = "2") Integer state,//默认查看已审核
                                      @RequestParam(required = false, defaultValue = "1") Integer page,
                                      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
//        if(!id.equals(departId))
//            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("部门id不匹配：" + id)), httpServletResponse);
        return commentService.showUnAuditComments(state, page, pageSize);
    }

//    @ApiOperation(value = "管理员新增商品价格浮动", produces = "application/json")
//    @ApiImplicitParams({
//            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
//            @ApiImplicitParam(paramType = "body", dataType = "RoleVo", name = "vo", value = "可修改的用户信息", required = true),
//            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path"),
//            @ApiImplicitParam(name="shopId", required = true, dataType="Integer", paramType="path")
//    })
//    @ApiResponses({
//            @ApiResponse(code = 0, message = "成功"),
//            @ApiResponse(code = 902, message = "商品浮动价格时间冲突"),
//    })
//    @Audit
//    @PostMapping("/shops/{shopId}/skus/{id}/floatPrices")
//    public Object add_floating_price(@Validated @RequestBody FloatPriceVo vo,
//                                     BindingResult bindingResult,
//                             @PathVariable("id") Long id,
//                             @PathVariable("shopId ") Long shopId,
//                             @LoginUser Long userId) {
////        logger.debug("insert role by userId:" + userId);
//        //校验前端数据
//        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
//        if (null != returnObject) {
////            logger.debug("validate fail");
//            return returnObject;
//        }
//        //检查该shop下有没有这个sku
//        FloatPrice floatPrice = vo.createFloatPrice();
//        floatPrice.setGoodsSkuId(id);
//        floatPrice.setBeginTime(vo.getBeginTime());
//        floatPrice.setEndTime(vo.getEndTime());
//        floatPrice.setQuantity(vo.getQuantity());
//        floatPrice.setActivityPrice(vo.getActivityPrice());
//        floatPrice.setCreatedBy(userId);
//        //floatPrice.setInvalidBy(????);
//        floatPrice.setValid(FloatPrice.Validation.getTypeByCode(0));
//        floatPrice.setGmtCreate(LocalDateTime.now());
//        ReturnObject retObject = floatPriceService.add_floating_price(floatPrice);
//        if (retObject.getData() != null) {
//            httpServletResponse.setStatus(HttpStatus.CREATED.value());
//            return Common.getRetObject(retObject);
//        } else {
//            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
//        }
//    }

    @ApiOperation(value="管理员失效商品价格浮动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name="authorization",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "shopId",value = "shop id",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "shop id",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @DeleteMapping("/shops/{shopId}/floatPrices/{id}")
    public Object invalidFloatPrice(@PathVariable Long shopId, @PathVariable Long id,
                                     @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                                     @Depart @ApiIgnore @RequestParam(required = false) Long departId)
    {
        //其实应该交给网关？
//        if(departId!=shopId)
//            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        return Common.decorateReturnObject(floatPriceService.invalidFloatPrice(id));
    }
}
