package cn.edu.xmu.activity.controller;

import cn.edu.xmu.activity.ActivityServiceApplication;
import cn.edu.xmu.activity.mapper.PresaleActivityPoMapper;
import cn.edu.xmu.activity.model.bo.ActivityStatus;
import cn.edu.xmu.activity.model.po.PresaleActivityPo;
import cn.edu.xmu.activity.model.vo.PresaleVo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * description: PresaleControllerTest
 * date: 2020/12/15 13:44
 * author: 杨铭
 * version: 1.0
 */
@SpringBootTest(classes = ActivityServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
//@Transactional
class PresaleControllerTest {

    @Autowired
    private MockMvc mvc;


    private WebTestClient webClient;


    @Autowired
    private PresaleActivityPoMapper presaleActivityPoMapper;


    private final String createTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        return token;
    }

    @Test
    public void getPresaleState() throws Exception {
        String token = createTestToken(1L, 0L, 100);
        String responseString=this.mvc.perform(MockMvcRequestBuilders.get("/presales/states")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":0,\"data\":[{\"code\":0,\"name\":\"已下线\"},{\"code\":1,\"name\":\"已上线\"},{\"code\":2,\"name\":\"已删除\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }


    /**
     * 设置所有条件，查询成功
     */
    @Order(0)
    @Test
    public void customerQueryPresales() throws Exception {

        String responseString=this.mvc.perform(MockMvcRequestBuilders.get("/presales"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":0,\"data\":{\"total\":3,\"pages\":1,\"pageSize\":3,\"page\":1,\"list\":[{\"id\":3100,\"name\":null,\"beginTime\":\"2021-01-04 00:39:21\",\"endTime\":\"2021-01-30 00:39:25\",\"payTime\":\"2023-08-02 00:39:16\",\"goodsSku\":{\"id\":3311,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5862230d20162.jpg\",\"inventory\":1,\"originalPrice\":3344,\"price\":3344,\"disabled\":0},\"shop\":{\"id\":1,\"name\":\"Nike\"}},{\"id\":3105,\"name\":null,\"beginTime\":\"2025-12-14 00:43:58\",\"endTime\":\"2020-12-26 00:43:51\",\"payTime\":\"2025-12-10 00:43:55\",\"goodsSku\":{\"id\":3311,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5862230d20162.jpg\",\"inventory\":1,\"originalPrice\":3344,\"price\":3344,\"disabled\":0},\"shop\":{\"id\":1,\"name\":\"Nike\"}},{\"id\":3107,\"name\":null,\"beginTime\":\"2027-12-17 00:45:21\",\"endTime\":\"2020-12-26 00:45:12\",\"payTime\":\"2027-12-08 00:45:16\",\"goodsSku\":{\"id\":3311,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5862230d20162.jpg\",\"inventory\":1,\"originalPrice\":3344,\"price\":3344,\"disabled\":0},\"shop\":{\"id\":1,\"name\":\"Nike\"}}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }


    /**
     * skuid不存在
     */
    @Test
    public void customerQueryPresales1() throws Exception {

        String responseString=this.mvc.perform(MockMvcRequestBuilders.get("/presales?skuId=98765"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse="{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":0,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * 赋值所有条件，成功
     */
    @Order(1)
    @Test
    public void adminQueryPresales() throws Exception {
        String token = createTestToken(1L, 0L, 100);
        String responseString=this.mvc.perform(MockMvcRequestBuilders.get("/goods/shops/0/presales")
                .header("authorization",token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":0,\"data\":{\"total\":11,\"pages\":1,\"pageSize\":11,\"page\":1,\"list\":[{\"id\":3100,\"name\":null,\"beginTime\":\"2021-01-04 00:39:21\",\"endTime\":\"2021-01-30 00:39:25\",\"payTime\":\"2023-08-02 00:39:16\",\"goodsSku\":{\"id\":3311,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5862230d20162.jpg\",\"inventory\":1,\"originalPrice\":3344,\"price\":3344,\"disabled\":0},\"shop\":{\"id\":1,\"name\":\"test\"}},{\"id\":3101,\"name\":null,\"beginTime\":\"2023-01-01 00:40:30\",\"endTime\":\"2020-12-26 00:40:39\",\"payTime\":\"2023-12-31 00:40:36\",\"goodsSku\":{\"id\":3311,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5862230d20162.jpg\",\"inventory\":1,\"originalPrice\":3344,\"price\":3344,\"disabled\":0},\"shop\":{\"id\":1,\"name\":\"test\"}},{\"id\":3102,\"name\":null,\"beginTime\":\"2020-12-02 00:41:34\",\"endTime\":\"2021-01-03 00:41:26\",\"payTime\":\"2020-12-24 00:41:30\",\"goodsSku\":{\"id\":3311,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5862230d20162.jpg\",\"inventory\":1,\"originalPrice\":3344,\"price\":3344,\"disabled\":0},\"shop\":{\"id\":1,\"name\":\"test\"}},{\"id\":3103,\"name\":null,\"beginTime\":\"2024-12-17 00:42:36\",\"endTime\":\"2020-12-26 00:42:28\",\"payTime\":\"2024-12-18 00:42:33\",\"goodsSku\":{\"id\":3311,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5862230d20162.jpg\",\"inventory\":1,\"originalPrice\":3344,\"price\":3344,\"disabled\":0},\"shop\":{\"id\":1,\"name\":\"test\"}},{\"id\":3104,\"name\":null,\"beginTime\":\"2019-03-08 00:42:54\",\"endTime\":\"2020-12-25 00:43:02\",\"payTime\":\"2019-12-24 00:42:59\",\"goodsSku\":{\"id\":3311,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5862230d20162.jpg\",\"inventory\":1,\"originalPrice\":3344,\"price\":3344,\"disabled\":0},\"shop\":{\"id\":1,\"name\":\"test\"}},{\"id\":3105,\"name\":null,\"beginTime\":\"2025-12-14 00:43:58\",\"endTime\":\"2020-12-26 00:43:51\",\"payTime\":\"2025-12-10 00:43:55\",\"goodsSku\":{\"id\":3311,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5862230d20162.jpg\",\"inventory\":1,\"originalPrice\":3344,\"price\":3344,\"disabled\":0},\"shop\":{\"id\":1,\"name\":\"test\"}},{\"id\":3106,\"name\":null,\"beginTime\":\"2026-10-26 00:44:33\",\"endTime\":\"2021-02-07 00:44:24\",\"payTime\":\"2026-01-02 00:44:29\",\"goodsSku\":{\"id\":3311,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5862230d20162.jpg\",\"inventory\":1,\"originalPrice\":3344,\"price\":3344,\"disabled\":0},\"shop\":{\"id\":1,\"name\":\"test\"}},{\"id\":3107,\"name\":null,\"beginTime\":\"2027-12-17 00:45:21\",\"endTime\":\"2020-12-26 00:45:12\",\"payTime\":\"2027-12-08 00:45:16\",\"goodsSku\":{\"id\":3311,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5862230d20162.jpg\",\"inventory\":1,\"originalPrice\":3344,\"price\":3344,\"disabled\":0},\"shop\":{\"id\":1,\"name\":\"test\"}},{\"id\":3108,\"name\":null,\"beginTime\":\"2027-11-30 00:45:51\",\"endTime\":\"2021-01-10 00:45:44\",\"payTime\":\"2028-12-26 00:45:48\",\"goodsSku\":{\"id\":3311,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5862230d20162.jpg\",\"inventory\":1,\"originalPrice\":3344,\"price\":3344,\"disabled\":0},\"shop\":{\"id\":1,\"name\":\"test\"}},{\"id\":3109,\"name\":null,\"beginTime\":\"2029-10-27 00:46:40\",\"endTime\":\"2020-12-18 00:46:30\",\"payTime\":\"2029-12-07 00:46:36\",\"goodsSku\":{\"id\":3311,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5862230d20162.jpg\",\"inventory\":1,\"originalPrice\":3344,\"price\":3344,\"disabled\":0},\"shop\":{\"id\":1,\"name\":\"test\"}},{\"id\":3110,\"name\":null,\"beginTime\":\"2020-12-01 00:47:26\",\"endTime\":\"2021-01-02 00:47:19\",\"payTime\":\"2020-12-09 00:47:22\",\"goodsSku\":{\"id\":3311,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5862230d20162.jpg\",\"inventory\":1,\"originalPrice\":3344,\"price\":3344,\"disabled\":0},\"shop\":{\"id\":1,\"name\":\"test\"}}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * skuid不存在
     */
    @Test
    public void adminQueryPresales1() throws Exception {
        String token = createTestToken(1L, 0L, 100);
        String responseString=this.mvc.perform(MockMvcRequestBuilders.get("/shops/1/presales?skuId=98765")
                .header("authorization",token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse="{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":0,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }


    /**
     * state字段不合法
     */
    @Order(3)
    @Test
    public void adminQueryPresales2() throws Exception {
        //String token = this.login("13088admin", "123456");
        String token = createTestToken(1L, 1L, 100);
        String responseString=this.mvc.perform(MockMvcRequestBuilders.get("/shops/1/presales?state=4")
                .header("authorization",token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno").value(ResponseCode.FIELD_NOTVALID.getCode()))
                .andReturn().getResponse().getContentAsString();

    }

    /**
     * beginTime > EndTime
     */
    @Test
    public void createPresaleOfSKU1() throws Exception {
        String token = createTestToken(1L, 0L, 100);
        PresaleVo presaleVo = new PresaleVo();
        presaleVo.setAdvancePayPrice(100L);
        presaleVo.setRestPayPrice(1000L);
        presaleVo.setName("testforcreatePresaleOfSKU");
        presaleVo.setQuantity(300);
        presaleVo.setBeginTime("2022-01-09 15:55:18");
        presaleVo.setPayTime("2022-01-11 15:55:18");
        presaleVo.setEndTime("2018-01-20 15:55:18");

        String Json = JacksonUtil.toJson(presaleVo);

        String responseString=this.mvc.perform(MockMvcRequestBuilders.post("/shops/1/skus/10/presales")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno").value(ResponseCode.FIELD_NOTVALID.getCode()))
                .andReturn().getResponse().getContentAsString();
        //String expectedString = "";
    }

    /**
     * test013
     * EndTime < now
     */
    @Test
    public void createPresaleOfSKU2() throws Exception {
        String token = createTestToken(1L, 0L, 100);
        PresaleVo presaleVo = new PresaleVo();
        presaleVo.setAdvancePayPrice(100L);
        presaleVo.setRestPayPrice(1000L);
        presaleVo.setName("testforcreatePresaleOfSKU");
        presaleVo.setQuantity(300);
        presaleVo.setBeginTime("2016-01-09 15:55:18");
        presaleVo.setPayTime("2017-01-11 15:55:18");
        presaleVo.setEndTime("2018-01-20 15:55:18");

        String Json = JacksonUtil.toJson(presaleVo);

        String responseString=this.mvc.perform(MockMvcRequestBuilders.post("/shops/1/skus/10/presales")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno").value(ResponseCode.FIELD_NOTVALID.getCode()))
                .andReturn().getResponse().getContentAsString();
    }


    /**
     * skuId不存在
     */
    @Test
    public void createPresaleOfSKU3() throws Exception {
        String token = createTestToken(1L, 0L, 100);
        PresaleVo presaleVo = new PresaleVo();
        presaleVo.setAdvancePayPrice(100L);
        presaleVo.setRestPayPrice(1000L);
        presaleVo.setName("testforcreatePresaleOfSKU");
        presaleVo.setQuantity(300);
        presaleVo.setBeginTime("2022-01-09 15:55:18");
        presaleVo.setPayTime("2022-01-11 15:55:18");
        presaleVo.setEndTime("2022-01-20 15:55:18");

        String Json = JacksonUtil.toJson(presaleVo);

        String responseString=this.mvc.perform(MockMvcRequestBuilders.post("/shops/1/skus/98765/presales")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno").value(ResponseCode.RESOURCE_ID_NOTEXIST.getCode()))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

    }



    /**
     * skuId存在，但不在此shop中
     */
    @Test
    public void createPresaleOfSKU4() throws Exception {
        String token = createTestToken(1L, 0L, 100);
        PresaleVo presaleVo = new PresaleVo();
        presaleVo.setAdvancePayPrice(100L);
        presaleVo.setRestPayPrice(1000L);
        presaleVo.setName("testforcreatePresaleOfSKU");
        presaleVo.setQuantity(300);
        presaleVo.setBeginTime("2022-01-09 15:55:18");
        presaleVo.setPayTime("2022-01-11 15:55:18");
        presaleVo.setEndTime("2022-01-20 15:55:18");

        String Json = JacksonUtil.toJson(presaleVo);

        String responseString=this.mvc.perform(MockMvcRequestBuilders.post("/shops/2/skus/3311/presales")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }


    /**
     * 此sku当前已经参与了其他预售活动
     */
    @Test
    public void createPresaleOfSKU5() throws Exception {
        String token = createTestToken(1L, 0L, 100);
        PresaleVo presaleVo = new PresaleVo();
        presaleVo.setAdvancePayPrice(100L);
        presaleVo.setRestPayPrice(1000L);
        presaleVo.setName("testforcreatePresaleOfSKU");
        presaleVo.setQuantity(300);
        presaleVo.setBeginTime("2021-01-01 15:55:18");
        presaleVo.setPayTime("2022-01-10 15:55:18");
        presaleVo.setEndTime("2023-01-12 15:55:18");

        String Json = JacksonUtil.toJson(presaleVo);

        String responseString=this.mvc.perform(MockMvcRequestBuilders.post("/shops/1/skus/3311/presales")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":906,\"errmsg\":\"预售活动状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * 成功
     */
    @Order(2)
    @Test
    public void createPresaleOfSKU() throws Exception {
        String token = createTestToken(1L, 0L, 100);
        PresaleVo presaleVo = new PresaleVo();
        presaleVo.setAdvancePayPrice(100L);
        presaleVo.setRestPayPrice(1000L);
        presaleVo.setName("testforcreatePresaleOfSKU");
        presaleVo.setQuantity(300);
        presaleVo.setBeginTime("2031-01-09 15:55:18");
        presaleVo.setPayTime("2031-01-09 23:55:18");
        presaleVo.setEndTime("2032-01-20 15:55:18");

        String Json = JacksonUtil.toJson(presaleVo);

        String responseString=this.mvc.perform(MockMvcRequestBuilders.post("/shops/1/skus/3311/presales")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":0,\"data\":{\"id\":3112,\"name\":null,\"beginTime\":\"2031-01-09 15:55:18\",\"endTime\":\"2032-01-20 15:55:18\",\"payTime\":\"2031-01-09 23:55:18\",\"goodsSku\":{\"id\":3311,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5862230d20162.jpg\",\"inventory\":1,\"originalPrice\":3344,\"price\":3344,\"disabled\":0},\"shop\":{\"id\":1,\"name\":\"Nike\"}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }



    /**
     * state = 已上线，则活动状态禁止修改
     */
    @Test
    public void modifyPresaleofSKU1() throws Exception {
        String token = createTestToken(1L, 0L, 100);
        String strategy = "teststrategy";
        String beginTime = "2020-12-20 15:55:18";
        String payTime = "2021-12-20 15:55:18";
        String endTime = "2022-01-05 15:55:18";

        PresaleVo presaleVo = new PresaleVo();
        presaleVo.setBeginTime(beginTime);
        presaleVo.setPayTime(payTime);
        presaleVo.setEndTime(endTime);

        String Json = JacksonUtil.toJson(presaleVo);

        String responseString=this.mvc.perform(put("/shops/1/presales/3100")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":906,\"errmsg\":\"预售活动状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }



    /**
     * shopId 无权限操作此 presaleId
     */
    @Test
    public void modifyPresaleofSKU2() throws Exception {
        String token = createTestToken(1L, 0L, 100);
        String strategy = "teststrategy";
        String beginTime = "2020-12-20 15:55:18";
        String payTime = "2021-12-20 15:55:18";
        String endTime = "2022-01-05 15:55:18";

        PresaleVo presaleVo = new PresaleVo();
        presaleVo.setBeginTime(beginTime);
        presaleVo.setPayTime(payTime);
        presaleVo.setEndTime(endTime);

        String Json = JacksonUtil.toJson(presaleVo);

        String responseString=this.mvc.perform(put("/shops/2/presales/3101")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }


    /**
     * 此预售已被逻辑删除
     */
    @Test
    public void modifyPresaleofSKU3() throws Exception {
        String token = createTestToken(1L, 0L, 100);
        String beginTime = "2020-12-20 15:55:18";
        String payTime = "2021-12-20 15:55:18";
        String endTime = "2022-01-05 15:55:18";

        PresaleVo presaleVo = new PresaleVo();
        presaleVo.setBeginTime(beginTime);
        presaleVo.setPayTime(payTime);
        presaleVo.setEndTime(endTime);

        String Json = JacksonUtil.toJson(presaleVo);

        String responseString=this.mvc.perform(put("/shops/1/presales/3102")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":906,\"errmsg\":\"预售活动状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }


    /**
     * 修改的beginTime > endTime
     */
    @Test
    public void modifyPresaleofSKU4() throws Exception {
        String token = createTestToken(1L, 0L, 100);
        String beginTime = "2020-01-20 15:55:18";
        String payTime = "2020-12-20 15:55:18";
        String endTime = "2019-01-09 15:55:18";

        PresaleVo presaleVo = new PresaleVo();
        presaleVo.setBeginTime(beginTime);
        presaleVo.setPayTime(payTime);
        presaleVo.setEndTime(endTime);

        String Json = JacksonUtil.toJson(presaleVo);

        String responseString=this.mvc.perform(put("/shops/1/presales/3103")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

    }





    /**
     * 修改的endTime早于now
     */
    @Test
    public void modifyPresaleofSKU5() throws Exception {
        String token = createTestToken(1L, 0L, 100);
        String beginTime = "2018-01-20 15:55:18";
        String payTime = "2018-12-01 15:55:18";
        String endTime = "2019-01-09 15:55:18";

        PresaleVo presaleVo = new PresaleVo();
        presaleVo.setBeginTime(beginTime);
        presaleVo.setPayTime(payTime);
        presaleVo.setEndTime(endTime);


        String Json = JacksonUtil.toJson(presaleVo);

        String responseString=this.mvc.perform(put("/shops/1/presales/3104")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

    }


    /**
     * state不为已下线，则无法删除
     */
    @Test
    public void cancelPresaleOfSKU1() throws Exception {
        String token = createTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(delete("/shops/1/presales/3105")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":906,\"errmsg\":\"预售活动状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

    }




    /**
     * 此shopId无权操作此presaleId
     */
    @Test
    public void cancelPresaleOfSKU2() throws Exception {
        String token = createTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(delete("/shops/2/presales/3106")
                .header("authorization",token))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }





    /**
     * 若预售状态不为已下线（已上线、已删除），则预售状态不允许上线
     */
    @Test
    public void putPresaleOnShelves1() throws Exception {
        String token = createTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(put("/shops/1/presales/3107/onshelves")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":906,\"errmsg\":\"预售活动状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }


    /**
     * 此shopId无权操作此presaleId
     */
    @Test
    public void putPresaleOnShelves2() throws Exception {
        String token = createTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(put("/shops/2/presales/3108/onshelves")
                .header("authorization",token))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }





    /**
     * 若预售状态不为已上线（已下线、已删除），则预售状态不允许下线
     */
    @Test
    public void putPresaleOffShelves1() throws Exception {
        String token = createTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(put("/shops/1/presales/3109/offshelves")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":906,\"errmsg\":\"预售活动状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

    }



    /**
     * 此shopId无权操作此presaleId
     */
    @Test
    public void putPresaleOffShelves2() throws Exception {
        String token = createTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(put("/shops/2/presales/3110/offshelves")
                .header("authorization",token))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }



    /**
     * 成功
     */
    @Order(3)
    @Test
    public void modifyPresaleofSKU() throws Exception {
        //读出旧的presale
        PresaleActivityPo oldPo = presaleActivityPoMapper.selectByPrimaryKey(2L);

        String token = createTestToken(1L, 0L, 100);

        String beginTime = "2021-12-20 15:55:18";
        String payTime = "2021-12-30 15:55:18";
        String endTime = "2022-01-05 15:55:18";

        PresaleVo presaleVo = new PresaleVo();
        presaleVo.setBeginTime(beginTime);
        presaleVo.setPayTime(payTime);
        presaleVo.setEndTime(endTime);

        String Json = JacksonUtil.toJson(presaleVo);

        String responseString=this.mvc.perform(put("/goods/shops/1/presales/3101")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\": 0, \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //检测数据库是否真的发生修改

        PresaleActivityPo newPo = presaleActivityPoMapper.selectByPrimaryKey(3101L);
        Assert.state(dtf.format(newPo.getPayTime()).equals(payTime), "paytime未修改！");//is true 则断
        Assert.state(dtf.format(newPo.getBeginTime()).equals(beginTime), "beginTime未修改！");
        Assert.state(dtf.format(newPo.getEndTime()).equals(endTime), "endTime未修改！");
    }

    /**
     * 成功
     */
    @Order(4)
    @Test
    public void putPresaleOnShelves() throws Exception {
        String token = createTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(put("/goods/shops/1/presales/3101/onshelves")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\": 0, \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
        //查看是否真的发生修改
        PresaleActivityPo newPo = presaleActivityPoMapper.selectByPrimaryKey(3101L);
        Assert.state(newPo.getState()== ActivityStatus.ON_SHELVES.getCode().byteValue(), "状态未修改！");
    }



    /**
     * 成功
     */
    @Order(5)
    @Test
    public void putPresaleOffShelves() throws Exception {
        String token = createTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(put("/goods/shops/1/presales/3101/offshelves")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\": 0, \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
        //查看是否真的发生修改
        PresaleActivityPo newPo = presaleActivityPoMapper.selectByPrimaryKey(3101L);
        Assert.state(newPo.getState()== ActivityStatus.OFF_SHELVES.getCode().byteValue(), "状态未修改！");

    }


    /**
     * 成功
     */
    @Test
    public void cancelPresaleOfSKU() throws Exception {
        String token = createTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(delete("/goods/shops/1/presales/3101")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\": 0, \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
        //查看是否真的发生修改
        PresaleActivityPo newPo = presaleActivityPoMapper.selectByPrimaryKey(3101L);
        Assert.state(newPo.getState()== ActivityStatus.DELETED.getCode().byteValue(), "状态未修改！");

    }


//    /**
//     * 根据timeline查询,还未开始的活动，timeline=0
//     */
//    @Test
//    public void customerQueryPresales2() throws Exception {
//        String token = createTestToken(1L, 0L, 100);
//        String responseString=this.mvc.perform(MockMvcRequestBuilders.get("/goods/presales")
//                .header("authorization",token))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.errno").value(ResponseCode.OK.getCode()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.errmsg").value("成功"))
//                .andReturn().getResponse().getContentAsString();
//        String expectedResponse="{\"errno\": 0, \"errmsg\": \"成功\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//
//    /**
//     * test004
//     * 根据timeline查询,明天开始的，timeline=1
//     */
//    @Test
//    public void customerQueryPresales3() throws Exception {
//        String token = createTestToken(1L, 0L, 100);
//        String responseString=this.mvc.perform(MockMvcRequestBuilders.get("/goods/presales")
//                .header("authorization",token))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        String expectedResponse="{\"errno\": 0, \"errmsg\": \"成功\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//
//    /**
//     * test005
//     * 根据timeline查询,正在进行中的，timeline=2
//     */
//    @Test
//    public void customerQueryPresales4() throws Exception {
//        String token = createTestToken(1L, 0L, 100);
//        String responseString=this.mvc.perform(MockMvcRequestBuilders.get("/goods/presales")
//                .header("authorization",token))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.errno").value(ResponseCode.OK.getCode()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.errmsg").value("成功"))
//                .andReturn().getResponse().getContentAsString();
//        String expectedResponse="{\"errno\": 0, \"errmsg\": \"成功\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//
//    /**
//     * test006
//     * 根据timeline查询,已经结束的，timeline=3
//     */
//    @Test
//    public void customerQueryPresales5() throws Exception {
//        String token = createTestToken(1L, 0L, 100);
//        String responseString=this.mvc.perform(MockMvcRequestBuilders.get("/goods/presales")
//                .header("authorization",token))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.errno").value(ResponseCode.OK.getCode()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.errmsg").value("成功"))
//                .andReturn().getResponse().getContentAsString();
//        String expectedResponse="{\"errno\": 0, \"errmsg\": \"成功\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }

//    /**
//     * test007
//     * skuid不存在
//     */
//    @Test
//    public void customerQueryPresales6() throws Exception {
//        String token = createTestToken(1L, 0L, 100);
//        String responseString=this.mvc.perform(MockMvcRequestBuilders.get("/goods/presales")
//                .header("authorization",token))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        String expectedResponse="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//
////    /**
////     * test008
////     * 查询第2页，page=2
////     */
////    @Test
////    public void customerQueryPresales7() throws Exception {
////        String token = createTestToken(1L, 0L, 100);
////        String responseString=this.mvc.perform(MockMvcRequestBuilders.get("/goods/presales")
////                .header("authorization",token))
////                .andExpect(MockMvcResultMatchers.status().isOk())
////                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.errno").value(ResponseCode.OK.getCode()))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.errmsg").value("成功"))
////                .andReturn().getResponse().getContentAsString();
////        String expectedResponse="{\"errno\": 0, \"errmsg\": \"成功\"}";
////        JSONAssert.assertEquals(expectedResponse,responseString,true);
////    }
//
//
//

//
//    /**
//     * test010
//     * state字段不合法
//     */
//    @Test
//    public void adminQueryPresales2() throws Exception {
//        String token = createTestToken(1L, 0L, 100);
//        String responseString=this.mvc.perform(MockMvcRequestBuilders.get("/goods/shops/1/presales")
//                .header("authorization",token))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        String expectedResponse="{\"errno\":503,\"errmsg\":\"字段不合法\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//
////    /**
////     * test011
////     * 按state=1查询 已上线
////     */
////    @Test
////    public void adminQueryPresales3() throws Exception {
////        String token = createTestToken(1L, 0L, 100);
////        String responseString=this.mvc.perform(MockMvcRequestBuilders.get("/goods/shops/0/presales")
////                .header("authorization",token))
////                .andExpect(MockMvcResultMatchers.status().isOk())
////                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
////                .andReturn().getResponse().getContentAsString();
////        String expectedResponse="{\"errno\": 0, \"errmsg\": \"成功\"}";
////        JSONAssert.assertEquals(expectedResponse,responseString,true);
////    }
//
//
//
//


























}