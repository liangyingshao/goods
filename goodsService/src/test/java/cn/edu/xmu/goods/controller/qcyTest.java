package cn.edu.xmu.goods.controller;

import cn.edu.xmu.goods.GoodsServiceApplication;
import cn.edu.xmu.goods.mapper.GoodsSpuPoMapper;
import cn.edu.xmu.goods.model.vo.CouponActivityCreateVo;
import cn.edu.xmu.goods.model.vo.GoodsSpuCreateVo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * description: SPU相关api测试类
 * date: 2020/11/30 0:38
 * author: 秦楚彦 24320182203254
 * version: 1.0
 */
@SpringBootTest(classes = GoodsServiceApplication.class)
@AutoConfigureMockMvc
//@Transactional
public class qcyTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    private GoodsSpuPoMapper spuPoMapper;

    /**
     * description: 创建测试用token
     * date: 2020/12/01 8:37
     * author: 秦楚彦 24320182203254
     */
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        return token;
    }


    /**
     * description: 店家新建商品SPU
     * date: 2020/12/01 8:37
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void addSpuTest() throws JSONException {
        GoodsSpuCreateVo vo = new GoodsSpuCreateVo();
        vo.setName("ipadair4");
        vo.setDescription("最新一代air系列产品");
        String token = creatTestToken(1L,1L,100);
        vo.setSpec("{\"id\":1,\"name\":\"ipadspec\", \"specItems\":{\"id\":1, \"name\":\"color\"},{\"id\":2, \"name\":\"memory\"}}");
        String spuJson = JacksonUtil.toJson(vo);
        String responseString=null;
        try{
            responseString=this.mvc.perform(post("/goods/shops/1/spus").header("authorization",token).contentType("application/json;charset=UTF-8").content(spuJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 查看一条商品SPU的详细信息 (成功)
     * date: 2020/12/03 19：28
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void showSpuTest1() throws JSONException {
        String responseString=null;
        try{
            responseString=this.mvc.perform(get("/goods/spus/290"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 查看一条商品SPU的详细信息 spuId不存在
     * date: 2020/12/03 19：33
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void showSpuTest2() throws JSONException {
        String responseString=null;
        try{
            responseString=this.mvc.perform(get("/goods/spus/1000"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 店家SPU上下架 不可重复上架/重复下架 shopId与spuId必须对应
     * date: 2020/12/01 19：10
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void onSaleSpuTest() throws Exception {
        String responseString=null;
        String token = creatTestToken(1L,1L,100);
        try{
            responseString=this.mvc.perform(put("/goods/shops/4/spus/333/onshelves").header("authorization",token).contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * description: 店家修改SPU
     * date: 2020/12/01 22：28
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void modifySpuTest() throws JSONException {
        GoodsSpuCreateVo vo = new GoodsSpuCreateVo();
        vo.setName("ipadpro");
        vo.setDescription("最新一代pro系列产品");
        String token = creatTestToken(1L,1L,100);
        vo.setSpec("{\"id\":1,\"name\":\"ipadspec\", \"specItems\":{\"id\":1, \"name\":\"color\"},{\"id\":2, \"name\":\"memory\"}}");
        String spuJson = JacksonUtil.toJson(vo);
        String responseString=null;
        try{
            responseString=this.mvc.perform(put("/goods/shops/1/spus/681").header("authorization",token).contentType("application/json;charset=UTF-8").content(spuJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * description: 将SPU加入二级分类 (成功)
     * date: 2020/12/02 01：09
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void addSpuCategoryTest1() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(put("/goods/shops/1/spus/681/categories/128")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8")
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 将SPU加入二级分类 (试图加入一级分类)
     * date: 2020/12/02 20：40
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void addSpuCategoryTest2() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(put("/goods/shops/1/spus/681/categories/125")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8")
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.errno").value(ResponseCode.CATEALTER_INVALID.getCode()))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 移除SPU分类 (成功)
     * date: 2020/12/02 11：00
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void removeSpuCategoryTest1() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(delete("/goods/shops/1/spus/681/categories/128")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.errno").value(ResponseCode.OK))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 移除SPU分类 (移出分类与SPU原分类不一致)
     * date: 2020/12/02 20：46
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void removeSpuCategoryTest2() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(delete("/goods/shops/1/spus/681/categories/131")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.errno").value(ResponseCode.CATEALTER_INVALID.getCode()))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 将SPU加入品牌 (成功)
     * date: 2020/12/02 22：52
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void addSpuBrandTest1() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(put("/goods/shops/1/spus/681/brands/110")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8")
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 将SPU加入品牌 (试图加入不存在的品牌)
     * date: 2020/12/02 22：52
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void addSpuBrandTest2() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(put("/goods/shops/1/spus/681/brands/120")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8")
            )
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.errno").value(ResponseCode.RESOURCE_ID_NOTEXIST.getCode()))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 移除SPU品牌 (成功)
     * date: 2020/12/02 22：52
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void removeSpuBrandTest1() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(delete("/goods/shops/1/spus/681/brands/110")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 移除SPU品牌 (移出品牌与SPU原品牌不一致)
     * date: 2020/12/02 22：52
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void removeSpuBrandTest2() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(delete("/goods/shops/1/spus/681/brands/109")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.errno").value(ResponseCode.BRANDALTER_INVALID.getCode()))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 逻辑删除商品SPU (成功)
     * date: 2020/12/03 01：24
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void deleteSpuBrandTest1() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(delete("/goods/shops/1/spus/681")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 上传商品SPU图片 (成功)
     * date: 2020/12/03 15：23
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void uploadFileSutccess() throws Exception{
        String token = creatTestToken(1L,1L,100);
        File file = new File("."+File.separator + "src" + File.separator + "test" + File.separator+"resources" + File.separator + "img" + File.separator+"timg.png");
        MockMultipartFile firstFile = new MockMultipartFile("img", "timg.png" , "multipart/form-data", new FileInputStream(file));
        String responseString = this.mvc.perform(MockMvcRequestBuilders
                .multipart("/goods/shops/1/spus/681/uploadImg")
                .file(firstFile)
                .header("authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }


//    /*
//     * 上传失败（id不存在）
//     */
//    @Test
//    public void UploadFileFail1() throws Exception{
//        String token = creatTestToken(1111L, 0L, 100);
//        File file = new File("."+File.separator + "src" + File.separator +  "test" + File.separator + "resources" + File.separator + "img" +File.separator+"timg.png");
//        MockMultipartFile firstFile = new MockMultipartFile("img", "timg.png" , "multipart/form-data", new FileInputStream(file));
//        String responseString = mvc.perform(MockMvcRequestBuilders
//                .multipart("/privilege/adminusers/uploadImg")
//                .file(firstFile)
//                .header("authorization", token)
//                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
//                .andExpect(MockMvcResultMatchers.status().isNotFound())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
//        JSONAssert.assertEquals(expectedResponse, responseString, true);
//    }
//
//    /*
//     * 上传失败（文件格式错误）
//     */
//    @Test
//    public void UploadFileFail2() throws Exception{
//        String token = creatTestToken(1L, 0L, 100);
//        File file = new File("."+File.separator + "src" + File.separator +  "test" + File.separator + "resources" + File.separator + "img" +File.separator+"文本文件.txt");
//        MockMultipartFile firstFile = new MockMultipartFile("img", "文本文件.txt" , "multipart/form-data", new FileInputStream(file));
//        String responseString = mvc.perform(MockMvcRequestBuilders
//                .multipart("/privilege/adminusers/uploadImg")
//                .file(firstFile)
//                .header("authorization", token)
//                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//        String expectedResponse = "{\"errno\":508,\"errmsg\":\"图片格式不正确\"}";
//        JSONAssert.assertEquals(expectedResponse, responseString, true);
//    }
//
//    /*
//     * 上传失败（文件格式错误，伪装成图片）
//     */
//    @Test
//    public void UploadFileFail3() throws Exception{
//        String token = creatTestToken(1L, 0L, 100);
//        File file = new File("."+File.separator + "src" + File.separator +  "test" + File.separator + "resources" + File.separator + "img" +File.separator+"伪装的图片.png");
//        MockMultipartFile firstFile = new MockMultipartFile("img", "伪装的图片.png" , "multipart/form-data", new FileInputStream(file));
//        String responseString = mvc.perform(MockMvcRequestBuilders
//                .multipart("/privilege/adminusers/uploadImg")
//                .file(firstFile)
//                .header("authorization", token)
//                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//        String expectedResponse = "{\"errno\":508,\"errmsg\":\"图片格式不正确\"}";
//        JSONAssert.assertEquals(expectedResponse, responseString, true);
//    }
//
//    /*
//     * 上传失败（文件格式错误）
//     */
//    @Test
//    public void UploadFileFail4() throws Exception{
//        String token = creatTestToken(1L, 0L, 100);
//        File file = new File("."+File.separator + "src" + File.separator +  "test" + File.separator + "resources" + File.separator + "img" +File.separator+"大小超限图片.jpg");
//        MockMultipartFile firstFile = new MockMultipartFile("img", "大小超限图片.jpg" , "multipart/form-data", new FileInputStream(file));
//        String responseString = mvc.perform(MockMvcRequestBuilders
//                .multipart("/privilege/adminusers/uploadImg")
//                .file(firstFile)
//                .header("authorization", token)
//                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//        String expectedResponse = "{\"errno\":509,\"errmsg\":\"图片大小超限\"}";
//        JSONAssert.assertEquals(expectedResponse, responseString, true);
//    }
    /**
     * description: 查看优惠活动详情 (成功)
     * date: 2020/12/04 20：27
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */

    @Test
    public void showCouponActivity1() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(get("/goods/shops/1/couponactivities/1")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 查看优惠活动详情 (活动不存在)
     * date: 2020/12/04 20：48
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void showCouponActivity2() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(get("/goods/shops/1/couponactivities/2")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 查看优惠活动详情 (shopId不匹配)
     * date: 2020/12/04 20：48
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void showCouponActivity3() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(get("/goods/shops/3/couponactivities/1")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isForbidden())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 新建己方优惠活动 (成功)
     * date: 2020/12/05 15:32
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void addCouponActivity1() throws JSONException {
        CouponActivityCreateVo vo=new CouponActivityCreateVo();
        vo.setName("appleSale");
        vo.setBeginTime(LocalDateTime.now().toString());
        vo.setEndTime(LocalDateTime.of(2020,12,31,10,0,0).toString());
        vo.setQuantity(10000);
        vo.setQuantityType(1);
        vo.setStrategy("{\"id\":1,\"name\":\"couponstrategy\", \"shresholds\":{\"type\":\"满减\",\"value\":\"200\",\"discount\":\"30\"}");
        vo.setValidTerm(0);
        String activityJson=JacksonUtil.toJson(vo);
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(post("/goods/shops/1/couponactivities")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8").content(activityJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 修改己方优惠活动 (成功)
     * date: 2020/12/05 20:04
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void modifyCouponActivity1() throws JSONException {
        CouponActivityCreateVo vo=new CouponActivityCreateVo();
        vo.setName("colaSale");
        vo.setBeginTime(LocalDateTime.now().toString());
        vo.setEndTime(LocalDateTime.of(2020,12,31,10,0,0).toString());
        vo.setQuantity(100);
        vo.setQuantityType(0);
        vo.setStrategy("{\"id\":1,\"name\":\"couponstrategy\", \"shresholds\":{\"type\":\"满减\",\"value\":\"200\",\"discount\":\"30\"}");
        vo.setValidTerm(0);
        String activityJson=JacksonUtil.toJson(vo);
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(put("/goods/shops/1/couponactivities/5")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8").content(activityJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 修改己方优惠活动 (优惠活动id不存在)
     * date: 2020/12/05 20:16
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void modifyCouponActivity2() throws JSONException {
        CouponActivityCreateVo vo=new CouponActivityCreateVo();
        vo.setName("appleSale");
        vo.setBeginTime(LocalDateTime.now().toString());
        vo.setEndTime(LocalDateTime.of(2020,12,31,10,0,0).toString());
        vo.setQuantity(100);
        vo.setQuantityType(0);
        vo.setStrategy("{\"id\":1,\"name\":\"couponstrategy\", \"shresholds\":{\"type\":\"满减\",\"value\":\"200\",\"discount\":\"30\"}");
        vo.setValidTerm(0);
        String activityJson=JacksonUtil.toJson(vo);
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(put("/goods/shops/1/couponactivities/100")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8").content(activityJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 下线己方优惠活动 (成功)
     * date: 2020/12/05 21:30
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void offlineCouponActivity1() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(delete("/goods/shops/1/couponactivities/5")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 下线己方优惠活动 (优惠活动已下线)
     * date: 2020/12/05 21:46
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void offlineCouponActivity2() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(delete("/goods/shops/1/couponactivities/5")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.errno").value(ResponseCode.ACTIVITYALTER_INVALID.getCode()))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 下线己方优惠活动 (不发优惠券类型的优惠活动 成功)
     * date: 2020/12/05 21:46
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void offlineCouponActivity3() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(delete("/goods/shops/1/couponactivities/10")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 下线己方优惠活动 (优惠活动无状态为【可用】优惠券)
     * date: 2020/12/05 21:46
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void offlineCouponActivity4() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(delete("/goods/shops/1/couponactivities/5")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
