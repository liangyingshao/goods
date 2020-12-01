package cn.edu.xmu.goods.controller;

import cn.edu.xmu.goods.model.bo.Comment;
import cn.edu.xmu.goods.model.vo.CommentAuditVo;
import cn.edu.xmu.goods.model.vo.CommentStateRetVo;
import cn.edu.xmu.goods.service.CommentService;
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
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * 业务: 评论模块控制器
 * @return
 * @author: 24320182203259 邵良颖
 * Created at: 2020-12-01 21:24
 * version: 1.0
 */
@Api(value = "商品服务", tags = "goods")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/goods", produces = "application/json;charset=UTF-8")
public class CommentController {

    private  static  final Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    CommentService commentService;

    /**
     * 业务: comment002买家新增sku的评论
     * @param id 订单明细的id
     * @param content
     * @param type
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
            @ApiImplicitParam(name="content", required = true, dataType="String", paramType="body"),
            @ApiImplicitParam(name="type", required = true, dataType="integer", paramType="body")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 903, message = "用户没有购买此商品")
    })
    @Audit
    @PostMapping("/orderitems/{id}/comments")
    @ResponseBody
    public Object addSkuComment(@PathVariable Long id, String content, Long type, @LoginUser @ApiIgnore @RequestParam(required = false, defaultValue = "0") Long userId){
        logger.debug("comment: id = "+ id+" userid: id = "+ userId + " type: " + type + " content: " + content);
        ReturnObject<VoObject> returnObject = commentService.addSkuComment(id, content, type, userId);

        return Common.decorateReturnObject(returnObject);
    }

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
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value ="用户token", required = true)
    })
    @ApiResponses({
    })
    @Audit
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
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "SKU Id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("/skus/{id}/comments")
    public Object selectAllPassComment(
            @PathVariable("id") Long id,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        logger.debug("selectAllPassComment: page = "+ page +"  pageSize ="+pageSize);
        ReturnObject<PageInfo<VoObject>> returnObject =  commentService.selectAllPassComment(id, page, pageSize);
        return Common.getPageRetObject(returnObject);
    }

    /**
     * 业务: comment004
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
    @PutMapping("/comments/{id}/confirm")
    public Object auditComment(@PathVariable("id") Long id, @RequestBody CommentAuditVo conclusion,
                               @Depart Long shopid) {
        ReturnObject returnObject=null;
        if(shopid==0)
        {
            returnObject=commentService.auditComment(id, conclusion.getConclusion());
        }
        else
        {
            return new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW);
        }
        return returnObject;
    }
}
