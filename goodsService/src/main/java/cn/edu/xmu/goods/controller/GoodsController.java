package cn.edu.xmu.goods.controller;

import cn.edu.xmu.goods.service.GoodsService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.*;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



/**
 * 权限控制器
 * @author Ming Qiu
 * Modified at 2020/11/5 13:21
 **/
@Api(value = "商品服务", tags = "goods")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/goods", produces = "application/json;charset=UTF-8")
public class GoodsController {

    private  static  final Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    GoodsService goodsService;


    /**
     * auth002: 用户重置密码
     * @return Object
     * @author 24320182203311 杨铭
     * Created at 2020/11/11 19:32
     */
    @ApiOperation(value="店家修改店铺信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "String", name = "name", value = "店铺名称", required = true)

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PutMapping("shops/{id}")
    @ResponseBody
    public Object modifyShop(@LoginUser @PathVariable Long id, String name) {


        ReturnObject returnObject = goodsService.modifyShop(id,name);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     *查询SKU
     * @param shopId
     * @param skuSn
     * @param spuId
     * @param spuSn
     * @param page
     * @param pageSize
     * @return Object
     */
    @ApiOperation(value = "查询SKU")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/skus")
    @ResponseBody
    public Object getSkuList(
            @RequestParam(required = false)Long shopId,
            @RequestParam(required = false)String skuSn,
            @RequestParam(required = false)Long spuId,
            @RequestParam(required = false)String spuSn,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize)
    {
        logger.debug("getSkuList:page="+page+" pageSize="+pageSize);
        ReturnObject returnObject=goodsService.getSkuList(shopId,skuSn,spuId,spuSn,page,pageSize);
        return Common.decorateReturnObject(returnObject);
    }


}

