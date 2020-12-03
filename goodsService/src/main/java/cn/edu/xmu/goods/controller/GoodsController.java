package cn.edu.xmu.goods.controller;

import cn.edu.xmu.goods.model.bo.Brand;
import cn.edu.xmu.goods.model.bo.GoodsCategory;
import cn.edu.xmu.goods.model.bo.GoodsSku;
import cn.edu.xmu.goods.model.vo.BrandVo;
import cn.edu.xmu.goods.model.vo.GoodsCategoryVo;
import cn.edu.xmu.goods.model.vo.GoodsSkuVo;
import cn.edu.xmu.goods.service.BrandService;
import cn.edu.xmu.goods.service.GoodsCategoryService;
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

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
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
    private HttpServletResponse httpServletResponse;

    @Autowired
    private BrandService brandService;

    @Autowired
    private GoodsCategoryService goodsCategoryService;

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
        ReturnObject<List> returnObject =  goodsCategoryService.getSubcategories(id);
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
    public Object insertBrand(@Validated @RequestBody GoodsCategoryVo vo, BindingResult bindingResult,
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
            return Common.getRetObject(retObject);
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

        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
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
            return Common.getRetObject(retObject);
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

        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
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



}

