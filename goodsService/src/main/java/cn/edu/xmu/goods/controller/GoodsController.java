package cn.edu.xmu.goods.controller;

import cn.edu.xmu.goods.model.bo.GoodsSku;
import cn.edu.xmu.goods.model.bo.GoodsSpu;
import cn.edu.xmu.goods.model.vo.GoodsSkuVo;
import cn.edu.xmu.goods.model.vo.GoodsSpuCreateVo;
import cn.edu.xmu.goods.model.vo.StateVo;
import cn.edu.xmu.goods.service.GoodsService;
import cn.edu.xmu.goods.service.SpuService;
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

    @Autowired
    SpuService spuService;

    @Autowired
    private HttpServletResponse httpServletResponse;

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

    /**
     * 获得sku的详细信息
     * @param id
     * @return Object
     */
    @ApiOperation(value="获得sku的详细信息")
    @ApiImplicitParam(paramType = "path",dataType = "Long",name="id",value = "sku id",required = true)
    @ApiResponse(code=0,message = "成功")
    @GetMapping("/skus/{id}")
    @ResponseBody
    public Object getSku(@PathVariable Long id)
    {
        logger.debug("getSku:id="+id);
        ReturnObject returnObject=goodsService.getSku(id);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * sku上传图片
     * @param shopId
     * @param id
     * @param file
     * @return Object
     */
    @ApiOperation(value="sku上传图片")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization",value = "用户token",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "shopId",value = "店铺id",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "sku id",required = true),
            @ApiImplicitParam(paramType = "formData", dataType = "file", name = "img", value ="文件", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 506, message = "该目录文件夹没有写入的权限"),
            @ApiResponse(code = 508, message = "图片格式不正确"),
            @ApiResponse(code = 509, message = "图片大小超限")
    })
    @Audit
    @PostMapping("/shops/{shopId}/skus/{id}/uploadImg")
    public Object uploadSkuImg(@PathVariable Long shopId,@PathVariable Long id, @RequestParam("img") MultipartFile file){
        logger.debug("uploadSkuImg: id = "+ id+" shopId="+shopId +" img=" + file.getOriginalFilename());
        ReturnObject returnObject = goodsService.uploadSkuImg(shopId,id,file);
        return Common.getNullRetObj(returnObject, httpServletResponse);
    }

    /**
     * 管理员或店家逻辑删除SKU
     * @param shopId
     * @param id
     * @return Object
     */
    @ApiOperation(value = "管理员或店家逻辑删除SKU")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name="authorization",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "shopId",value = "shop id",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "sku id",required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @DeleteMapping("/shops/{shopId}/skus/{id}")
    public Object deleteSku(@PathVariable Long shopId,@PathVariable Long id)
    {
        logger.debug("deleteSku: id = "+ id+" shopId="+shopId);
        ReturnObject returnObject=goodsService.deleteSku(shopId,id);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员或店家修改SKU信息
     * @param shopId
     * @param id
     * @param vo
     * @return Object
     */
    @ApiOperation(value = "管理员或店家修改SKU信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name="authorization",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "shopId",value = "shop id",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "sku id",required = true),
            @ApiImplicitParam(paramType = "body",dataType = "GoodsSkuVo",name = "vo",value = "可修改的SKU信息",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PutMapping("/shops/{shopId}/skus/{id}")
    public Object modifySKU(@PathVariable Long shopId,@PathVariable Long id,@Validated @RequestBody GoodsSkuVo vo,BindingResult bindingResult)
    {
        logger.debug("modifySKU: id = "+ id+" shopId="+shopId+" vo="+vo);
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }
        GoodsSku sku=vo.createGoodsSku();
        sku.setId(id);
        ReturnObject retObject=goodsService.modifySku(shopId,sku);
        if (retObject.getData() != null) {
            return Common.getRetObject(retObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }
    }

    /**
     * spu001: 获得商品SPU的所有状态
     * @return Object
     * @author 24320182203254 秦楚彦
     * Created at 2020/11/29 21:02
     */
    @ApiOperation(value="获得商品SPU的所有状态",produces="application/json")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @GetMapping("spus/states")
    @ResponseBody
    public Object getgoodspustate() {

        GoodsSpu.SpuState[] spustates=GoodsSpu.SpuState.class.getEnumConstants();
        List<StateVo> stateVos=new ArrayList<StateVo>();
        for(int i=0;i<spustates.length;i++){
            stateVos.add(new StateVo(spustates[i]));

        }

        return ResponseUtil.ok(new ReturnObject<List>(stateVos).getData());
    }


    /**
     * spu003 业务: 查看一条商品SPU的详细信息（无需登录）
     * @param id
     * @return  ReturnObject
     * @author 24320182203254 秦楚彦
     * Created at 2020/11/30 08：41
     */
    @ApiOperation(value="查看一条商品SPU的详细信息（无需登录）",produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "商品SPUid", required = true)

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("spus/{id}")
    @ResponseBody
    public Object showSpu(@PathVariable Long id) {
        Object returnObject=null;
        ReturnObject<Object> spu = spuService.showSpu(id);
        logger.debug("findSpuById: spu="+spu.getData()+" code="+spu.getCode());
        if (!spu.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)) {
            return spu;
        } else {
            returnObject = Common.getNullRetObj(new ReturnObject<>(spu.getCode(), spu.getErrmsg()), httpServletResponse);
        }

        return returnObject;
    }

    /**
     * spu004 业务: 店家新建商品SPU
     * @param id 商铺ID
     * @param vo 新增SPU视图
     * @param userId 当前用户ID
     * @return  ReturnObject
     * @author 24320182203254 秦楚彦
     * Created at 2020/11/30 22：19
     */
    @ApiOperation(value="店家新建商品SPU",produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "商铺id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "", name = "body", value = "SPU详细信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),

    })
    @Audit // 需要认证
    @PostMapping("shops/{id}/spus")
    @ResponseBody
    public Object addSpu(@Validated @RequestBody GoodsSpuCreateVo vo,
                         @PathVariable Long id,
                         BindingResult bindingResult,
                         @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                         @Depart @ApiIgnore @RequestParam(required = false) Long departId

    ) {
        logger.debug("insert SPU by shopId:" + id);
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.debug("validate fail");
            return returnObject;
        }
        GoodsSpu spu=vo.createSpu();
        spu.setShopId(id);
        spu.setGmtCreated(LocalDateTime.now());
        ReturnObject retObject = spuService.addSpu(spu);
        if (retObject.getData() != null) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return retObject;
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }

    }

    /**
     * spu008 业务: 上架商品SPU
     * @param id 商品SPUID
     * @param shopId 店铺ID
     * @param userId 当前用户ID
     * @return  Object
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/01 15：36
     */
    @ApiOperation(value="店家商品上架",produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "商铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "SpuaId", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),

    })
    @Audit // 需要认证
    @PutMapping("shops/{shopId}/spus/{id}/onshelves")
    @ResponseBody
    public Object putGoodsOnSale(@PathVariable Long shopId,@PathVariable Long id,BindingResult bindingResult,
                                 @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                                 @Depart @ApiIgnore @RequestParam(required = false) Long departId) {
        logger.debug("put SPU onsale by shopId:" + shopId+ " spuId" + id);
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.debug("validate fail");
            return returnObject;
        }


        GoodsSpu spu=new GoodsSpu();
        spu.setShopId(shopId);
        spu.setId(id);
        spu.setGmtModified(LocalDateTime.now());
        ReturnObject retObject = spuService.addSpu(spu);
        //校验是否为该商铺管理员
        if(shopId!=departId)
            return  Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        if(retObject.getData()!=null){
            return Common.getRetObject(retObject);
        }else{
            return  Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }

    }

    /**
     * spu009 业务: 下架商品SPU
     * @param id 商品SPUID
     * @param shopId 店铺ID
     * @param userId 当前用户ID
     * @return  Object
     * @author 24320182203254 秦楚彦
     * Created at 2020/11/01 15：36
     */
    @ApiOperation(value="店家商品下架",produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "商铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "SpuaId", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),

    })
    @Audit // 需要认证
    @PutMapping("shops/{shopId}/spus/{id}/offshelves")
    @ResponseBody
    public Object putOffGoodsOnSale(@PathVariable Long shopId,@PathVariable Long id,BindingResult bindingResult,
                                    @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                                    @Depart @ApiIgnore @RequestParam(required = false) Long departId) {
        logger.debug("put SPU offsale by shopId:" + shopId+ " spuId" + id);
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.debug("validate fail");
            return returnObject;
        }
        //校验是否为该商铺管理员

        GoodsSpu spu=new GoodsSpu();
        spu.setShopId(shopId);
        spu.setId(id);
        spu.setGmtModified(LocalDateTime.now());
        ReturnObject retObject = spuService.putGoodsOnSale(spu);
        if(retObject.getData()!=null){
            return Common.getRetObject(retObject);
        }else{
            return  Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }

    }

}

