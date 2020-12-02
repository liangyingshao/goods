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
     * description: 将SPU加入二级分类
     * date: 2020/12/02 01：09
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void addSpuCategoryTest() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        String requireJson=null;
        try{
            responseString=this.mvc.perform(put("/goods/shops/1/spus/681/categories/125")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8")
                    .content(requireJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                    .andExpect(jsonPath("$.data").exists())
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 移除SPU分类
     * date: 2020/12/02 11：00
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void removeSpuCategoryTest() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        String requireJson=null;
        try{
            responseString=this.mvc.perform(delete("/goods/shops/1/spus/681/categories/125")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8")
                    .content(requireJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                    .andExpect(jsonPath("$.data").exists())
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
