package cn.edu.xmu.goods.controller;

import cn.edu.xmu.goods.GoodsServiceApplication;
import cn.edu.xmu.goods.mapper.GoodsSpuPoMapper;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
     * 获取SPU所有状态 成功
     *
     * @throws Exception
     */
    @Test
    public void getAllState() throws Exception {
        String responseString = this.mvc.perform(get("/goods/spus/states"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{ \"errno\": 0, \"data\": [ { \"name\": \"上架\", \"code\": 0 }, { \"name\": \"下架\", \"code\": 1 } ], \"errmsg\": \"成功\" }";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
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
}
