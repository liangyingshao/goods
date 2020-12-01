package cn.edu.xmu.goods.controller;

import cn.edu.xmu.goods.GoodsServiceApplication;
import cn.edu.xmu.ooad.util.JwtHelper;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * 业务: 测试comment的API
 * @param null
 * @return
 * @author: 24320182203259 邵良颖
 * Created at: 2020-12-01 21:23
 * version: 1.0
 */
@SpringBootTest(classes = GoodsServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
//@Transactional
public class CommentControllerTest {
    @Autowired
    private MockMvc mvc;

    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        return token;
    }

    @Test
    public void addSkuComment1() throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(post("/goods/orderitems/1/comments").header("authorization",token).contentType("application/json;charset=UTF-8").queryParam("type", "0").queryParam("content", "购物体验良好"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\": 0, \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
        //一要检测返回值是否符合预期
        //二要检查数据库的值是否符合预期
    }

    @Test
    public void getcommentState1() throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(get("/goods/comments/states").header("authorization",token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":0,\"data\":[{\"name\":\"待审核\",\"code\":0},{\"name\":\"审核通过\",\"code\":1},{\"name\":\"审核不通过\",\"code\":2}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    @Test
    public void selectAllPassComment1() {
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        try {
            responseString = this.mvc.perform(get("/goods/skus/1/comments?page=1&pageSize=2").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":0,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void auditComment() throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        String json = "{\"conclusion\":\"true\"}";
        String responseString = this.mvc.perform(
                put("/goods/comments/2/confirm?conclusion=true")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    public void showComment() {
        String responseString = null;
        String token = creatTestToken(1L, 1L, 100);
        try {
            responseString = this.mvc.perform(get("/goods/comments?page=1&pageSize=2").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":2,\"page\":1,\"list\":[{\"id\":2,\"customerId\":1,\"goodsSkuId\":1,\"orderitemId\":1,\"type\":0,\"content\":null,\"state\":0,\"gmtCreate\":\"2020-12-01T21:20:48\",\"gmtModified\":null},{\"id\":3,\"customerId\":1,\"goodsSkuId\":1,\"orderitemId\":1,\"type\":0,\"content\":null,\"state\":0,\"gmtCreate\":\"2020-12-01T21:26:54\",\"gmtModified\":null}]},\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
