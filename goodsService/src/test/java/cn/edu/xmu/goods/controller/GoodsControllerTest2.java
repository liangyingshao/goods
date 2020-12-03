package cn.edu.xmu.goods.controller;

import cn.edu.xmu.goods.mapper.GoodsCategoryPoMapper;
import cn.edu.xmu.goods.model.vo.GoodsCategoryVo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.goods.GoodsServiceApplication;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * description: 商品类目测试类
 * date: 2020/12/2 20:46
 * author: 张悦
 * version: 1.0
 */
@SpringBootTest(classes = GoodsServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GoodsControllerTest2 {

    private String content;

    private static final Logger logger = LoggerFactory.getLogger(GoodsControllerTest1.class);
    @Autowired
    private MockMvc mvc;

    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        return token;
    }

    @Autowired
    private GoodsCategoryPoMapper goodsCategoryPoMapper;


    /**
     * description: 根据种类ID获取商品下一级分类信息(成功)
     * version: 1.0 
     * date: 2020/12/2 23:43 
     * author: 张悦 
     * 
     * @param 
     * @return void
     */ 
    @Test
    public void listSubcategories1() throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(get("/goods/categories/122/subcategories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/listSubcategories.json")));
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * description: 根据分类ID获取商品下一级分类信息(分类ID不存在)
     * version: 1.0
     * date: 2020/12/2 23:43
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    public void listSubcategories2() throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(get("/goods/categories/199/subcategories"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * description: 新增商品一级类目,pid=0 （成功）
     * version: 1.0
     * date: 2020/12/2 20:47
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    public void insertGoodsCategoryTest1()throws Exception {
        GoodsCategoryVo vo = new GoodsCategoryVo();
        vo.setName("test");
        String token = creatTestToken(1L, 0L, 100);
        String goodsCategoryJson = JacksonUtil.toJson(vo);

        System.out.print(goodsCategoryJson);

        String responseString = this.mvc.perform(post("/goods/shops/0/categories/0/subcategories").header("authorization", token).contentType("application/json;charset=UTF-8").content(goodsCategoryJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse ="{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "        \"pid\":0,\n" +
                "        \"name\": \"test\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * description: 新增商品二级类目,pid=122 （成功）
     * version: 1.0
     * date: 2020/12/2 20:47
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    public void insertSubGoodsCategoryTest1()throws Exception {
        GoodsCategoryVo vo = new GoodsCategoryVo();
        vo.setName("test");
        String token = creatTestToken(1L, 0L, 100);
        String goodsCategoryJson = JacksonUtil.toJson(vo);

        System.out.print(goodsCategoryJson);

        String responseString = this.mvc.perform(post("/goods/shops/0/categories/122/subcategories").header("authorization", token).contentType("application/json;charset=UTF-8").content(goodsCategoryJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse ="{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "        \"pid\":122,\n" +
                "        \"name\": \"test\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * description: 新增商品二级类目, pid不存在
     * version: 1.0
     * date: 2020/12/2 20:47
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    public void insertSubGoodsCategoryTest2()throws Exception {
        GoodsCategoryVo vo = new GoodsCategoryVo();
        vo.setName("test");
        String token = creatTestToken(1L, 0L, 100);
        String goodsCategoryJson = JacksonUtil.toJson(vo);

        System.out.print(goodsCategoryJson);

        String responseString = this.mvc.perform(post("/goods/shops/0/categories/9/subcategories").header("authorization", token).contentType("application/json;charset=UTF-8").content(goodsCategoryJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * description: 新增商品类目，类目名称已存在
     * version: 1.0
     * date: 2020/12/2 20:47
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    public void insertGoodsCategoryTest2() {
        GoodsCategoryVo vo = new GoodsCategoryVo();
        vo.setName("艺术品");
        String token = creatTestToken(1L, 0L, 100);
        String goodsCategoryJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/goods/shops/0/categories/122/subcategories").header("authorization", token).contentType("application/json;charset=UTF-8").content(goodsCategoryJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":991,\"errmsg\":\"类目名称已存在：艺术品\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * description: 新增商品类目 类目名称为空
     * version: 1.0
     * date: 2020/12/2 20:48
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    public void insertGoodsCategoryTest3() throws Exception{
        GoodsCategoryVo vo = new GoodsCategoryVo();
        vo.setName("");
        String token = creatTestToken(1L, 0L, 100);
        String goodsCategoryJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;
        responseString = this.mvc.perform(post("/goods/shops/0/categories/122/subcategories").header("authorization", token).contentType("application/json;charset=UTF-8").content(goodsCategoryJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":503,\"errmsg\":\"类目名称不能为空;\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }


    /**
     * description: 修改类目信息(类目ID不存在)
     * version: 1.0 
     * date: 2020/12/2 19:44 
     * author: 张悦
     */ 
    @Test
    public void modifyNilCategory() throws Exception {
        GoodsCategoryVo vo = new GoodsCategoryVo();
        vo.setName("test");
        String token = creatTestToken(1L, 0L, 100);
        String goodsCategoryJson = JacksonUtil.toJson(vo);

        String responseString = this.mvc.perform(
                put("/goods/shops/0/categories/99")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8")
                        .content(goodsCategoryJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }


    /**
     * description: 修改类目信息 (类目名称重复)
     * version: 1.0
     * date: 2020/12/2 19:45
     * author: 张悦
     */
    @Test
    public void modifyCategoryDuplicateName() throws Exception {

        GoodsCategoryVo vo = new GoodsCategoryVo();
        vo.setName("艺术品");
        String token = creatTestToken(1L, 0L, 100);
        String goodsCategoryJson = JacksonUtil.toJson(vo);

        String responseString  = this.mvc.perform(
                put("/goods/shops/0/categories/123")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8")
                        .content(goodsCategoryJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();


        String expectedResponse = "{\"errno\":991,\"errmsg\":\"类目名称已存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }


    /**
     * description: 删除类目 (成功)
     * version: 1.0
     * date: 2020/12/2 19:45
     * author: 张悦
     */
    @Test
    public void deleteCategoryTest1() {
        //测试数据
        String token = creatTestToken(1L, 0L, 100);
        String expectedResponse = "";
        String responseString = null;
        //测试删除成功
        try {
            responseString = this.mvc.perform(delete("/goods/shops/0/categories/122").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * description: 删除类目（ id不存在)
     * version: 1.0 
     * date: 2020/12/2 23:00 
     * author: 张悦 
     * 
     * @param 
     * @return void
     */ 
    @Test
    public void deleteCategoryTest2() {
        String token = creatTestToken(1L, 0L, 100);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(delete("/goods/shops/0/categories/1").header("authorization", token))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
