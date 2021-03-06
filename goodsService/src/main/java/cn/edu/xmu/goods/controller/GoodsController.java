package cn.edu.xmu.goods.controller;

import cn.edu.xmu.goods.model.bo.*;
import cn.edu.xmu.goods.model.vo.*;
import cn.edu.xmu.goods.service.*;
import cn.edu.xmu.goods.service.impl.IGoodsServiceImpl;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.*;
import cn.edu.xmu.oomall.other.service.IFootprintService;
import cn.edu.xmu.oomall.other.service.IShareService;
import cn.edu.xmu.privilegeservice.client.IUserService;
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
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Api(value = "商品服务", tags = "goods")
@RestController /*Restful的Controller对象*/
//@RequestMapping(produces = "application/json;charset=UTF-8")
@RequestMapping(value = "/goods", produces = "application/json;charset=UTF-8")
public class GoodsController {

    private  static  final Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    GoodsService goodsService;

    @Autowired
    CommentService commentService;

    @Autowired
    SpuService spuService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private BrandService brandService;

    @Autowired
    private GoodsCategoryService goodsCategoryService;

    @DubboReference(check = false)
    private IFootprintService iFootprintService;

    @DubboReference(check = false)
    private IShareService iShareService;

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
        return Common.getPageRetObject(returnObject);
    }

    /**
     * 获得sku的详细信息
     * @param id
     * @return Object
     */
    @ApiOperation(value="获得sku的详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path",dataType = "Long",name="id",value = "sku id",required = true)
    })

    @ApiResponse(code=0,message = "成功")
    @Audit
    @GetMapping("/skus/{id}")
    @ResponseBody
    public Object getSku(@PathVariable Long id,@LoginUser @ApiIgnore @RequestParam(required = false) Long userId)
    {
        logger.debug("getSku:id="+id);
        ReturnObject returnObject=goodsService.getSku(id);
        if(userId!=null&&returnObject.getCode().equals(ResponseCode.OK)){
            try{
                iFootprintService.postFootprint(userId, id);
            }
            catch (Exception e)
            {
                logger.error("IFootPrint未启动");
            }
        }
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
    public Object uploadSkuImg(@PathVariable Long shopId,@PathVariable Long id,
                               @RequestParam("img") MultipartFile file,BindingResult bindingResult,
                               @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                               @Depart @ApiIgnore @RequestParam(required = false) Long departId){
        logger.debug("uploadSkuImg: id = "+ id+" shopId="+shopId +" img=" + file.getOriginalFilename());
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }
        if(departId!=0&&!Objects.equals(departId, shopId))
            return Common.getRetObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
        ReturnObject retObject = goodsService.uploadSkuImg(shopId,id,file);
        return Common.getNullRetObj(retObject, httpServletResponse);
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
    public Object deleteSku(@PathVariable Long shopId, @PathVariable Long id,
                            @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                            @Depart @ApiIgnore @RequestParam(required = false) Long departId)
    {
        logger.debug("deleteSku: id = "+ id+" shopId="+shopId);
        ReturnObject retObject=goodsService.deleteSku(shopId,id);
        return Common.decorateReturnObject(retObject);
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
    public Object modifySKU(@PathVariable Long shopId,@PathVariable Long id,
                            @Validated @RequestBody GoodsSkuVo vo,BindingResult bindingResult,
                            @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                            @Depart @ApiIgnore @RequestParam(required = false) Long departId)
    {
        logger.debug("modifySKU: id = "+ id+" shopId="+shopId+" vo="+vo);
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }
        if(departId!=0&&departId!=shopId)
            return Common.getRetObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
        GoodsSku sku=vo.createGoodsSku();
        sku.setId(id);
        ReturnObject retObject=goodsService.modifySku(shopId,sku);
        if (retObject.getData() != null) {
            return Common.decorateReturnObject(retObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }
    }

    /**
     * 管理员新增商品价格浮动
     * @param shopId
     * @param id
     * @param vo
     * @param bindingResult
     * @return Object
     */
    @ApiOperation(value="管理员新增商品价格浮动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name="authorization",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "shopId",value = "shop id",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "sku id",required = true),
            @ApiImplicitParam(paramType = "body",dataType = "FloatPriceVo",name = "vo",value = "可修改的信息",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PostMapping("/shops/{shopId}/skus/{id}/floatPrices")
    public Object add_floating_price(@PathVariable Long shopId, @PathVariable Long id,
                                     @Validated @RequestBody FloatPriceVo vo, BindingResult bindingResult,
                                     @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                                     @Depart @ApiIgnore @RequestParam(required = false) Long departId)
    {
        logger.error("进入新增价格浮动项controller层，检查是否有参数错误");
        if(vo.getBeginTime().isAfter(vo.getEndTime()))return Common.getRetObject(new ReturnObject<>(ResponseCode.Log_Bigger));

        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.error("参数错误");
            return returnObject;
        }
        logger.error("没有参数错误");
//        if(departId!=0&&departId!=shopId)
//            return Common.getRetObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));

        FloatPrice floatPrice=vo.createFloatPrice();
        floatPrice.setGoodsSkuId(id);
        floatPrice.setValid(FloatPrice.Validation.VALID);
        floatPrice.setCreatedBy(userId);
        floatPrice.setInvalidBy(userId);
        ReturnObject retObject=goodsService.addFloatPrice(shopId,floatPrice,userId);
        if (retObject.getData() != null) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.decorateReturnObject(retObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }
    }

    /**
     * 管理员添加新的SKU到SPU里
     * @param shopId
     * @param id
     * @param vo
     * @param bindingResult
     * @param userId
     * @param departId
     * @return Object
     */
    @ApiOperation(value = "管理员添加新的SKU到SPU里")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name="authorization",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "shopId",value = "shop id",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "sku id",required = true),
            @ApiImplicitParam(paramType = "body",dataType = "GoodsSkuBySpuVo",name = "vo",value = "可修改的信息",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PostMapping("/shops/{shopId}/spus/{id}/skus")
    public Object createSKU(@PathVariable Long shopId, @PathVariable Long id,
                            @Validated @RequestBody GoodsSkuBySpuVo vo, BindingResult bindingResult,
                            @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                            @Depart @ApiIgnore @RequestParam(required = false) Long departId)
    {
        logger.debug("createSKU: id = "+ id+" shopId="+shopId+" vo="+vo);
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }
        if(departId!=0&&!Objects.equals(departId, shopId))return Common.getRetObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
        GoodsSku sku=vo.createGoodsSku();
        sku.setGoodsSpuId(id);
        sku.setState(GoodsSku.State.OFFSHELF);
        sku.setDisabled(GoodsSku.Disable.OPEN);
        ReturnObject retObject=goodsService.createSKU(shopId,sku);
        if (retObject.getData() != null) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.getRetObject(retObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }
    }

    /**
     * 获得商品SKU的所有状态
     * @return Object
     */
    @ApiOperation(value="获得商品SKU的所有状态",produces="application/json")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @GetMapping("skus/states")
    @ResponseBody
    public Object getgoodskustate() {
        logger.debug("getgoodskustate");
        GoodsSku.State[] states=GoodsSku.State.class.getEnumConstants();
        List<GoodsSkuStateRetVo> stateVos=new ArrayList<GoodsSkuStateRetVo>();
        for (GoodsSku.State state : states) stateVos.add(new GoodsSkuStateRetVo(state));
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
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "商品SPUid", required = true)

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("spus/{id}")
    @ResponseBody
    public Object showSpu(@PathVariable Long id) {
        Object returnObject=null;
        ReturnObject spu = spuService.showSpu(id);
        //return Common.getRetObject(spu);
        return Common.decorateReturnObject(spu);
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
            return ResponseUtil.ok(retObject.getData());
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }
    }

    /**
     * spu006 业务: 修改商品SPU
     * @param id 商品SPUID
     * @param shopId 店铺ID
     * @param userId 当前用户ID
     * @return  Object
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/01 22：00
     */
    @ApiOperation(value="修改商品SPU",produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "商铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "SpuId", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "GoodsSpuCreateVo", name = "body", value = "可修改的信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),

    })
    @Audit // 需要认证
    @PutMapping("shops/{shopId}/spus/{id}")
    @ResponseBody
    public Object modifyGoodsSpu(@Validated @RequestBody GoodsSpuCreateVo vo,BindingResult bindingResult,
                                 @PathVariable Long shopId,@PathVariable Long id,
                                 @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                                 @Depart @ApiIgnore @RequestParam(required = false) Long departId) {
        logger.debug("modify SPU by shopId:" + shopId+ " spuId:" + id);
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.debug("validate fail");
            return returnObject;
        }
        //根据请求参数生成spuVo
        GoodsSpu spu=vo.createSpu();
        spu.setShopId(shopId);
        spu.setId(id);
        spu.setGmtModified(LocalDateTime.now());
        ReturnObject retObject = spuService.modifyGoodsSpu(spu);
        return Common.decorateReturnObject(retObject);


    }

    /**
     * spu008 店家商品上架
     * @param shopId
     * @param id
     * @param userId
     * @param departId
     * @return Object
     */
    @ApiOperation(value="店家商品上架",produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "商铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "SkuId", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),

    })
    @Audit // 需要认证
    @PutMapping("shops/{shopId}/skus/{id}/onshelves")
    @ResponseBody
    public Object putGoodsOnSale(@PathVariable Long shopId,@PathVariable Long id,
                                 @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                                 @Depart @ApiIgnore @RequestParam(required = false) Long departId) {
        logger.debug("put SKU onsale by shopId:" + shopId+ " skuId:" + id);

        //校验是否为该商铺管理员
        if(departId!=0&&shopId!=departId)
            return  Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE), httpServletResponse);
        ReturnObject retObject = goodsService.putGoodsOnSale(shopId,id);
        if (retObject.getData() != null) {
            return Common.decorateReturnObject(retObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }
    }

    /**
     * 店家商品下架
     * @param shopId
     * @param id
     * @param userId
     * @param departId
     * @return Object
     */
    @ApiOperation(value="店家商品下架",produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "商铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "SkuId", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),

    })
    @Audit // 需要认证
    @PutMapping("shops/{shopId}/skus/{id}/offshelves")
    @ResponseBody
    public Object putOffGoodsOnSale(@PathVariable Long shopId,@PathVariable Long id,
                                    @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                                    @Depart @ApiIgnore @RequestParam(required = false) Long departId) {
        logger.debug("put SKU offsale by shopId:" + shopId+ " skuId:" + id);

        //校验是否为该商铺管理员
        if(departId!=0&&departId!=shopId)
            return Common.getRetObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
        ReturnObject retObject = goodsService.putOffGoodsOnSale(shopId,id);
        if (retObject.getData() != null) {
            return Common.decorateReturnObject(retObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }
    }

    /**
     * spu010 业务: 将SPU加入二级分类/若已有分类则修改
     * @param spuId 商品SPUID
     * @param shopId 店铺ID
     * @param id 商品分类ID
     * @param userId 当前用户ID
     * @return  Object
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/01 22：49
     */
    @ApiOperation(value="将SPU加入分类",produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "商铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "spuId", value = "SpuId", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "categoryId", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),

    })
    @Audit // 需要认证
    @PutMapping("shops/{shopId}/spus/{spuId}/categories/{id}")
    //@ResponseBody
    public Object addSpuCategory(@PathVariable Long shopId,@PathVariable Long spuId,@PathVariable Long id,
                                 @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                                 @Depart @ApiIgnore @RequestParam(required = false) Long departId) {
        logger.debug("add SPU to a category by shopId:" + shopId+ " spuId:" + spuId + " cateId" + id);

        GoodsSpu spu=new GoodsSpu();
        spu.setShopId(shopId);
        spu.setId(spuId);
        spu.setCategoryId(id);
        spu.setGmtModified(LocalDateTime.now());

        ReturnObject retObject = spuService.addSpuCategory(spu);

        return Common.decorateReturnObject(retObject);

    }

    /**
     * spu011 业务: 将SPU移出分类（变为无分类商品）
     * @param spuId 商品SPUID
     * @param shopId 店铺ID
     * @param id 商品分类ID
     * @param userId 当前用户ID
     * @return  Object
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/02 10：29
     */
    @ApiOperation(value="将SPU移出分类",produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "商铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "spuId", value = "SpuId", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "categoryId", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),

    })
    @Audit // 需要认证
    @DeleteMapping("shops/{shopId}/spus/{spuId}/categories/{id}")
    //@ResponseBody
    public Object removeSpuCategory(@PathVariable Long shopId,@PathVariable Long spuId,@PathVariable Long id,
                                    @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                                    @Depart @ApiIgnore @RequestParam(required = false) Long departId) {
        logger.debug("remove SPU from a category by shopId:" + shopId+ " spuId:" + spuId + " cateId" + id);

        GoodsSpu spu=new GoodsSpu();
        spu.setShopId(shopId);
        spu.setId(spuId);
        spu.setCategoryId(id);
        spu.setGmtModified(LocalDateTime.now());

        ReturnObject retObject = spuService.removeSpuCategory(spu);
        return Common.decorateReturnObject(retObject);

    }
    /**
     * spu012 业务: 将SPU加入品牌/若已有品牌则修改
     * @param spuId 商品SPUID
     * @param shopId 店铺ID
     * @param id 商品分类ID
     * @param userId 当前用户ID
     * @return  Object
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/02 22：27
     */
    @ApiOperation(value="将SPU加入品牌",produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "商铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "spuId", value = "SpuId", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "brandId", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),

    })
    @Audit // 需要认证
    @PutMapping("shops/{shopId}/spus/{spuId}/brands/{id}")
    //@ResponseBody
    public Object addSpuBrand(@PathVariable Long shopId,@PathVariable Long spuId,@PathVariable Long id,
                                 @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                                 @Depart @ApiIgnore @RequestParam(required = false) Long departId) {
        logger.debug("add SPU to a brand by shopId:" + shopId+ " spuId:" + spuId + " brandId" + id);

        GoodsSpu spu=new GoodsSpu();
        spu.setShopId(shopId);
        spu.setId(spuId);
        spu.setBrandId(id);
        spu.setGmtModified(LocalDateTime.now());

        ReturnObject retObject = spuService.addSpuBrand(spu);
        return Common.decorateReturnObject(retObject);

    }

    /**
     * spu013 业务: 将SPU移出品牌（变为无分类商品）
     * @param spuId 商品SPUID
     * @param shopId 店铺ID
     * @param id 商品分类ID
     * @param userId 当前用户ID
     * @return  Object
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/02 22：27
     */
    @ApiOperation(value="将SPU移出分类",produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "商铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "spuId", value = "SpuId", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "brandId", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),

    })
    @Audit // 需要认证
    @DeleteMapping("shops/{shopId}/spus/{spuId}/brands/{id}")
    //@ResponseBody
    public Object removeSpuBrand(@PathVariable Long shopId,@PathVariable Long spuId,@PathVariable Long id,
                                 @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                                 @Depart @ApiIgnore @RequestParam(required = false) Long departId) {
        logger.debug("remove SPU from a brand by shopId:" + shopId+ " spuId:" + spuId + " brandId" + id);

        GoodsSpu spu=new GoodsSpu();
        spu.setShopId(shopId);
        spu.setId(spuId);
        spu.setBrandId(id);
        spu.setGmtModified(LocalDateTime.now());

        ReturnObject retObject = spuService.removeSpuBrand(spu);
        return Common.decorateReturnObject(retObject);

    }

    /**
     * spu007 业务: 店家逻辑删除商品SPU
     * @param id 商品SPUID
     * @param shopId 店铺ID
     * @param userId 当前用户ID
     * @return  Object
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/02 23：57
     */
    @ApiOperation(value="将SPU移出分类",produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "商铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "SpuId", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),

    })
    @Audit // 需要认证
    @DeleteMapping("shops/{shopId}/spus/{id}")
    //@ResponseBody
    public Object deleteGoodsSpu(@PathVariable Long shopId,@PathVariable Long id,
                                 @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                                 @Depart @ApiIgnore @RequestParam(required = false) Long departId) {
        logger.debug("logical delete SPU by shopId:" + shopId+ " spuId:" + id);


        ReturnObject retObject = spuService.deleteGoodsSpu(shopId,id);
        return Common.decorateReturnObject(retObject);

    }

    /**
     * description: 根据种类ID获取商品下一级分类信息
     * version: 1.0
     * date: 2020/12/2 23:23
     * author: 张悦
     *
     * @param id
     * @return java.lang.Object
     */
    @ApiOperation(value = "根据种类ID获取商品下一级分类信息")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作的资源id不存在")
    })
    //@Audit
    @GetMapping("/categories/{id}/subcategories")
    public Object getSubcategories(@PathVariable Long id){
        ReturnObject returnObject =  goodsCategoryService.getSubcategories(id);
        return  Common.decorateReturnObject(returnObject);
    }

    /**
     * description: 管理员新增商品类目
     * version: 1.0
     * date: 2020/12/2 19:00
     * author: 张悦
     *
     * @param vo
     * @param bindingResult
     * @param userId
     * @param departId
     * @return java.lang.Object
     */
    @ApiOperation(value = "管理员新增商品类目", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "GoodsCategoryVo", name = "vo", value = "可修改的类目信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 991, message = "类目名称已存在")
    })
    @Audit
    @PostMapping("/shops/{shopId}/categories/{id}/subcategories")
    public Object insertCategory(@Validated @RequestBody GoodsCategoryVo vo, BindingResult bindingResult,
                              @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                              @Depart @ApiIgnore @RequestParam(required = false) Long departId,
                              @PathVariable Long id) {
        logger.debug("insert category by userId:" + userId);
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.debug("validate fail");
            return returnObject;
        }
        GoodsCategory goodsCategory = vo.createGoodsCategory();
        goodsCategory.setGmtCreate(LocalDateTime.now());
        ReturnObject retObject = goodsCategoryService.insertGoodsCategory(goodsCategory,id);
        if (retObject.getData() != null) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.decorateReturnObject(retObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }
    }

    /**
     * description: 修改商品类目信息
     * version: 1.0
     * date: 2020/12/2 23:38
     * author: 张悦
     *
     * @param id
     * @param vo
     * @param bindingResult
     * @param userId
     * @param departId
     * @param httpServletResponse
     * @return java.lang.Object
     */
    @ApiOperation(value = "管理员修改商品类目信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="String", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 991, message = "类目名称已存在"),
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("/shops/{shopId}/categories/{id}")
    public Object changeCategory(@PathVariable Long id, @Validated @RequestBody GoodsCategoryVo vo, BindingResult bindingResult, @LoginUser Long userId, @Depart Long departId,
                                 HttpServletResponse httpServletResponse){
        logger.debug("changeCategory: id = "+ id +" vo" + vo);
        //logger.debug("getAllPrivs: userId = " + userId +" departId = "+departId);
        /* 处理参数校验错误 */
        Object o = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(o != null){
            return o;
        }
        ReturnObject<VoObject> returnObject = goodsCategoryService.changeCategory(id, vo);
        return Common.decorateReturnObject(returnObject);
    }


    /**
     * description: 删除商品类目信息
     * version: 1.0
     * date: 2020/12/2 23:38
     * author: 张悦
     *
     * @param id
     * @return java.lang.Object
     */
    @ApiOperation(value = "删除商品类目信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit // 需要认证
    @DeleteMapping("/shops/{shopId}/categories/{id}")
    public Object deleteCategory(@PathVariable Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("deleteCategory: id = "+ id);
        }
        ReturnObject returnObject = goodsCategoryService.deleteCategory(id);
        //return Common.getRetObject(returnObject);
        return Common.decorateReturnObject(returnObject);
    }


    /**
     * description: 管理员新增品牌
     * version: 1.0
     * date: 2020/12/2 19:00
     * author: 张悦
     *
     * @param vo
     * @param bindingResult
     * @param userId
     * @param departId
     * @return java.lang.Object
     */
    @ApiOperation(value = "新增品牌", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "BrandVo", name = "vo", value = "可修改的品牌信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 990, message = "品牌名称已存在")
    })
    @Audit
    @PostMapping("/shops/{shopId}/brands")
    public Object insertBrand(@Validated @RequestBody BrandVo vo, BindingResult bindingResult,
                              @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                              @Depart @ApiIgnore @RequestParam(required = false) Long departId) {
        logger.debug("insert brand by userId:" + userId);
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.debug("validate fail");
            return returnObject;
        }
        Brand brand = vo.createBrand();
        brand.setGmtCreate(LocalDateTime.now());
        ReturnObject retObject = brandService.insertBrand(brand);
        if (retObject.getData() != null) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
           return Common.decorateReturnObject(retObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }
    }

    /**
     * description: 上传品牌图片
     * version: 1.0
     * date: 2020/12/3 14:57
     * author: 张悦
     *
     * @param id
     * @param file
     * @return java.lang.Object
     */
    @ApiOperation(value="上传品牌图片")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization",value = "用户token",required = true),
            @ApiImplicitParam(paramType = "formData", dataType = "file", name = "img", value ="文件", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 506, message = "该目录文件夹没有写入的权限"),
            @ApiResponse(code = 508, message = "图片格式不正确"),
            @ApiResponse(code = 509, message = "图片大小超限")
    })
    @Audit
    @PostMapping("/shops/{shopId}/brands/{id}/uploadImg")
    public Object uploadBrandImg(@PathVariable Long id, @RequestParam("img") MultipartFile file){
        logger.debug("uploadBrandImg: id = "+ id+" shopId="+0 +" img=" + file.getOriginalFilename());
        ReturnObject returnObject = brandService.uploadBrandImg(id,file);
        return Common.getNullRetObj(returnObject, httpServletResponse);
    }

    /**
     * description: 查看所有品牌
     * version: 1.0
     * date: 2020/12/1 22:52
     * author: 张悦
     *
     * @param page
     * @param pageSize
     * @return java.lang.Object
     */
    @ApiOperation(value = "查看所有品牌")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    //@Audit
    @GetMapping("brands")
    public Object findAllBrands(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize){

        logger.debug("getAllBrands: page = "+ page +"  pageSize ="+pageSize);

        page = (page == null)?1:page;
        pageSize = (pageSize == null)?10:pageSize;

        logger.debug("getAllBrands: page = "+ page +"  pageSize ="+pageSize);
        ReturnObject<PageInfo<VoObject>> returnObject = brandService.findAllBrands(page, pageSize);
        return Common.getPageRetObject(returnObject);
    }

    /**
     * description: 修改商品品牌信息
     * version: 1.0
     * date: 2020/12/2 23:38
     * author: 张悦
     *
     * @param id
     * @param vo
     * @param bindingResult
     * @param userId
     * @param departId
     * @param httpServletResponse
     * @return java.lang.Object
     */
    @ApiOperation(value = "管理员修改商品牌信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="String", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 990, message = "品牌名称已存在"),
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PutMapping("/shops/{shopId}/brands/{id}")
    public Object changeBrand(@PathVariable Long id, @Validated @RequestBody BrandVo vo, BindingResult bindingResult, @LoginUser Long userId, @Depart Long departId,
                              HttpServletResponse httpServletResponse){
        logger.debug("changeBrand: id = "+ id +" vo" + vo);
        //logger.debug("getAllPrivs: userId = " + userId +" departId = "+departId);
        /* 处理参数校验错误 */
        Object o = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(o != null){
            return o;
        }
        ReturnObject<VoObject> returnObject = brandService.changeBrand(id, vo);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * description: 删除品牌信息
     * version: 1.0
     * date: 2020/12/2 23:38
     * author: 张悦
     *
     * @param id
     * @return java.lang.Object
     */
    @ApiOperation(value = "删除品牌信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit // 需要认证
    @DeleteMapping("/shops/{shopId}/brands/{id}")
    public Object deleteBrand(@PathVariable Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("deleteBrand: id = "+ id);
        }
        ReturnObject returnObject = brandService.deleteBrand(id);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 查看一条分享商品SKU的详细信息（需登录）
     * @param sid
     * @param id
     * @param userId
     * @return Object
     */
    @ApiOperation(value = "查看一条分享商品SKU的详细信息（需登录）")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="sid", required = true, dataType="Integer", paramType="path",value = "分享ID"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path",value = "商品SKU ID")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit // 需要认证
    @GetMapping("/share/{sid}/skus/{id}")
    @ResponseBody
    public Object getShareSku(@PathVariable Long sid, @PathVariable Long id,
                              @LoginUser @ApiIgnore @RequestParam(required = false) Long userId)
    {
        logger.debug("share,进入controller层");
        ReturnObject returnObject = iShareService.shareUserSkuMatch(sid,id,userId);
        logger.debug("share,返回+"+returnObject.getData());
        if(returnObject.getData().equals(Boolean.TRUE))
            returnObject=goodsService.getShareSku(id);
        else return Common.decorateReturnObject(new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE));
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * spu上传图片
     * @param shopId
     * @param id
     * @param file
     * @return Object
     */
    @ApiOperation(value="spu上传图片")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization",value = "用户token",required = true),
            @ApiImplicitParam(paramType = "formData", dataType = "file", name = "img", value ="文件", required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "shopId",value = "店铺id",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "spu id",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 506, message = "该目录文件夹没有写入的权限"),
            @ApiResponse(code = 508, message = "图片格式不正确"),
            @ApiResponse(code = 509, message = "图片大小超限")
    })
    @Audit
    @PostMapping("/shops/{shopId}/spus/{id}/uploadImg")
    public Object uploadSpuImg(@PathVariable Long shopId,@PathVariable Long id,
                               @RequestParam("img") MultipartFile file,
                               @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                               @Depart @ApiIgnore @RequestParam(required = false) Long departId){
        logger.debug("uploadSpuImg: id = "+ id+" shopId="+shopId +" img=" + file.getOriginalFilename());

        GoodsSpu spu=new GoodsSpu();
        spu.setShopId(shopId);
        spu.setId(id);
        spu.setGmtModified(LocalDateTime.now());
        ReturnObject retObject = spuService.uploadSpuImg(spu,file);
        return Common.getNullRetObj(retObject, httpServletResponse);
    }

}

