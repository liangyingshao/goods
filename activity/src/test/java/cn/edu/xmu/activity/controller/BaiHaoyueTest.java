package cn.edu.xmu.activity.controller;

import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;


import java.nio.charset.StandardCharsets;


/**
 *
 * @param  * @param null
 * @return
 * @author 24320182203161 Bai Haoyue 白皓月
 * @date: 2020/12/13 19:33
 */
@SpringBootTest(classes = Application.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class BaiHaoyueTest {

    //@Value("${public-test.managementgate}")
    private String managementGate="192.168.43.73:8881";

    //@Value("${public-test.mallgate}")
    private String mallGate="192.168.43.73:8880";
    private WebTestClient manageClient;

    private WebTestClient mallClient;


    @BeforeEach
    public void setUp(){

        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://"+managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://"+mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }

    private String userLogin(String userName, String password) throws Exception{

        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        return JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
    }

    private String adminLogin(String userName, String password) throws Exception{

        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        return JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
    }

    /** 查看某活动商品
     * 异常1
     * 活动不存在
     * */
    @Test
    @Order(0)
    public void getcouponsku1() throws Exception {
        byte[] queryResponseString = mallClient.get().uri("/couponactivities/0/skus")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "  \"errno\": 504,\n" +
//                "  \"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse1,new String(queryResponseString, "UTF-8"), false);
    }
    /** 查看某活动商品
     * 正常
     *
     * */
    @Test
    @Order(1)
    public void getcouponsku2() throws Exception {
        byte[] queryResponseString = mallClient.get().uri("/couponactivities/1/skus")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 3,\n" +
                "    \"pages\": 1,\n" +
                "    \"pageSize\": 3,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 333,\n" +
                "        \"name\": \"+\",\n" +
                "        \"sknSn\": null,\n" +
                "        \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_5866629229288.jpg\",\n" +
                "        \"inventory\": 100,\n" +
                "        \"originalPrice\": 3600,\n" +
                "        \"price\": 3600,\n" +
                "        \"disable\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 444,\n" +
                "        \"name\": \"+\",\n" +
                "        \"sknSn\": null,\n" +
                "        \"imageUrl\": \"http://47.52.88.176/file/images/201808/file_5b74f132d4900.jpg\",\n" +
                "        \"inventory\": 100,\n" +
                "        \"originalPrice\": 3499,\n" +
                "        \"price\": 3499,\n" +
                "        \"disable\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 555,\n" +
                "        \"name\": \"+\",\n" +
                "        \"sknSn\": null,\n" +
                "        \"imageUrl\": \"http://47.52.88.176/file/images/201711/file_5a12f4794a992.jpg\",\n" +
                "        \"inventory\": 1,\n" +
                "        \"originalPrice\": 60000,\n" +
                "        \"price\": 60000,\n" +
                "        \"disable\": false\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse1,new String(queryResponseString, "UTF-8"), false);
    }
    /** 查看本店已下线优惠活动
     * 异常1
     * 店铺不属于用户
     * */
    @Test
    @Order(2)
    public void getunderline1() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE2MjM1NzU1NTlZIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgxMzc4NzUsInVzZXJJZCI6MSwiaWF0IjoxNjA4MTM0Mjc1fQ.pPt1eKnY36zI2sZcBbiN57FTdGaq3Pj_CbbXdJUzt2U"
                //this.adminLogin("13088admin","123456")
                ;
        byte[] queryResponseString = manageClient.get().uri("/shops/2/couponactivities/invalid").header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "  \"errno\": 505,\n" +
//                "  \"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse1,new String(queryResponseString, "UTF-8"), false);
    }
    /** 查看本店已下线优惠活动
     * 正常1
     * 默认分页
     * */
    @Test
    @Order(3)
    public void getunderline2() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE2MjM1NzU1NTlZIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgxMzc4NzUsInVzZXJJZCI6MSwiaWF0IjoxNjA4MTM0Mjc1fQ.pPt1eKnY36zI2sZcBbiN57FTdGaq3Pj_CbbXdJUzt2U"
                //this.adminLogin("13088admin","123456")
                ;
        byte[] queryResponseString = manageClient.get().uri("/shops/0/couponactivities/invalid").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 3,\n" +
                "    \"pages\": 1,\n" +
                "    \"pageSize\": 3,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1,\n" +
                "        \"name\": \"string\",\n" +
                "        \"beginTime\": \"2022-12-13 08:02:14\",\n" +
                "        \"endTime\": \"2024-12-13 08:02:14\",\n" +
                "        \"couponTime\": \"2022-12-13 08:02:14\",\n" +
                "        \"quantity\": 0,\n" +
                "        \"imageUrl\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 4,\n" +
                "        \"name\": \"string\",\n" +
                "        \"beginTime\": \"2022-12-13 08:02:14\",\n" +
                "        \"endTime\": \"2024-12-13 08:02:14\",\n" +
                "        \"couponTime\": \"2022-12-13 08:02:14\",\n" +
                "        \"quantity\": 0,\n" +
                "        \"imageUrl\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 9,\n" +
                "        \"name\": \"string\",\n" +
                "        \"beginTime\": \"2020-12-13 16:33:50\",\n" +
                "        \"endTime\": \"2021-12-29 08:28:50\",\n" +
                "        \"couponTime\": \"2020-12-14 08:28:50\",\n" +
                "        \"quantity\": 0,\n" +
                "        \"imageUrl\": null\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse1,new String(queryResponseString, "UTF-8"), false);
    }
    /** 查看本店已下线优惠活动
     * 正常2
     * pagesize=1 page=2
     * */
    @Test
    @Order(4)
    public void getunderline3() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        byte[] queryResponseString = manageClient.get().uri((uriBuilder -> uriBuilder.path("/shops/0/couponactivities/invalid")
                .queryParam("page",2)
                .queryParam("pageSize",1)
                .build())
        ).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 3,\n" +
                "    \"pages\": 3,\n" +
                "    \"pageSize\": 1,\n" +
                "    \"page\": 2,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 4,\n" +
                "        \"name\": \"string\",\n" +
                //      "        \"beginTime\": \"2022-12-13T08:02:14\",\n" +
                //      "        \"endTime\": \"2024-12-13T08:02:14\",\n" +
                //     "        \"couponTime\": \"2022-12-13T08:02:14\",\n" +
                "        \"quantity\": 0,\n" +
                "        \"imageUrl\": null\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse1,new String(queryResponseString, "UTF-8"), false);
    }
    /** 查看本已上线优惠活动
     * 正常1
     *默认分页
     * */
    @Test
    @Order(5)
    public void getonline1() throws Exception {
        byte[] queryResponseString = mallClient.get().uri(uriBuilder -> uriBuilder.path("/couponactivities")
                .queryParam("shopId",2)
                .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 0,\n" +
                "    \"pages\": 0,\n" +
                "    \"pageSize\": 0,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": []\n" +
                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse1,new String(queryResponseString, "UTF-8"), false);

    }
    /** 查看本已上线优惠活动
     * 正常1
     *默认分页
     * */
    @Test
    @Order(6)
    public void getonline2() throws Exception {
        byte[] queryResponseString = mallClient.get().uri(uriBuilder -> uriBuilder.path("/couponactivities")
                .queryParam("shopId",0)
                .queryParam("timeline",2)
                .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 1,\n" +
                "    \"pages\": 1,\n" +
                "    \"pageSize\": 1,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 8,\n" +
                "        \"name\": \"string\",\n" +
                "        \"beginTime\": \"2020-12-13 08:33:50\",\n" +
                "        \"endTime\": \"2021-12-29 00:28:50\",\n" +
                "        \"couponTime\": \"2020-12-14 00:28:50\",\n" +
                "        \"quantity\": 0,\n" +
                "        \"imageUrl\": null\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse1,new String(queryResponseString, "UTF-8"), false);

    }

    /** 新建优惠活动
     * 非法情况1
     * 店铺不属于该用户
     * */
    @Test
    @Order(7)
    public void createCouponActivity1() throws Exception {
        String token1 = this.adminLogin("13088admin", "123456");
        String json="{\n" +
                "  \"beginTime\": \"2022-12-15T07:05:33.976Z\",\n" +
                "  \"couponTime\": \"2022-12-15T07:05:33.976Z\",\n" +
                "  \"endTime\": \"2023-12-15T07:05:33.976Z\",\n" +
                "  \"name\": \"string\",\n" +
                "  \"quantitiyType\": 0,\n" +
                "  \"quantity\": 0,\n" +
                "  \"strategy\": \"string\",\n" +
                "  \"validTerm\": 0\n" +
                "}";
        byte[] responseString1 = manageClient.post().uri("/shops/2/couponactivities")
                .header("authorization",token1)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\n" +
                "  \"errno\": 505,\n" +
//                "  \"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString1, "UTF-8"), false);
    }
    /**
     * 成功
     *
     * */
    @Test
    @Order(8)
    public void createCouponActivity2() throws Exception {
        String token1 = this.adminLogin("13088admin", "123456");
        String json="{\n" +
                "  \"beginTime\": \"2022-12-15T07:05:33.976Z\",\n" +
                "  \"couponTime\": \"2022-12-15T07:05:33.976Z\",\n" +
                "  \"endTime\": \"2023-12-15T07:05:33.976Z\",\n" +
                "  \"name\": \"string\",\n" +
                "  \"quantitiyType\": 0,\n" +
                "  \"quantity\": 0,\n" +
                "  \"strategy\": \"string\",\n" +
                "  \"validTerm\": 0\n" +
                "}";
        byte[] responseString = manageClient.post().uri("/shops/0/couponactivities")
                .header("authorization",token1)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse =   "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"id\": 10,\n" +
                "    \"name\": \"string\",\n" +
                "    \"beginTime\": \"2022-12-15 07:05:33.976\",\n" +
                "    \"endTime\": \"2023-12-15 07:05:33.976\",\n" +
                "    \"couponTime\": \"2022-12-15 07:05:33.976\",\n" +
                "    \"state\": 0,\n" +
                "    \"shopId\": 0,\n" +
                "    \"quantity\": 0,\n" +
                "    \"validTerm\": 0,\n" +
                "    \"imageUrl\": null,\n" +
                "    \"strategy\": \"string\",\n" +
                "    \"createdBy\": {\n" +
                "      \"userId\": 1,\n" +
                "      \"userName\": \"13088admin\"\n" +
                "    },\n" +
                "    \"modiBy\": {\n" +
                "      \"userId\": null,\n" +
                "      \"userName\": null\n" +
                "    },\n" +
                "    \"gmtModified\": null,\n" +
                "    \"quantitiyType\": 0\n" +
                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /** 修改优惠活动
     * 非法情况1
     * 店铺不属于该用户
     * */
    @Test
    @Order(9)
    public void modifyCouponActivity1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String json="{\n" +
                "  \"beginTime\": \"2022-12-15T07:41:46.909Z\",\n" +
                "  \"endTime\": \"2023-12-15T07:41:46.909Z\",\n" +
                "  \"name\": \"modify\",\n" +
                "  \"quantity\": 0,\n" +
                "  \"strategy\": \"string\"\n" +
                "}";
        byte[] responseString = manageClient.put().uri("/shops/2/couponactivities/10")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse =  "{\n" +
                "  \"errno\": 505,\n" +
//                "  \"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /** 修改优惠活动
     * 非法情况2
     * 活动不属于该店铺
     * */
    @Test
    @Order(10)
    public void modifyCouponActivity2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String json="{\n" +
                "  \"beginTime\": \"2022-12-15T07:41:46.909Z\",\n" +
                "  \"endTime\": \"2023-12-15T07:41:46.909Z\",\n" +
                "  \"name\": \"modify\",\n" +
                "  \"quantity\": 0,\n" +
                "  \"strategy\": \"string\"\n" +
                "}";
        byte[] responseString = manageClient.put().uri("/shops/0/couponactivities/7")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse =  "{\n" +
                "  \"errno\": 505,\n" +
//                "  \"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /** 修改优惠活动
     * 正常
     *
     * */
    @Order(11)
    @Test
    public void modifyCouponActivity3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String json="{\n" +
                "  \"beginTime\": \"2020-12-16T16:33:50\",\n" +
                "  \"endTime\": \"2021-12-29T08:28:50\",\n" +
                "  \"name\": \"modify\",\n" +
                "  \"quantity\": 0,\n" +
                "  \"strategy\": \"string\"\n" +
                "}";
        byte[] responseString1 = manageClient.put().uri("/shops/0/couponactivities/9")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse =  "{\n" +
                "  \"errno\": 0,\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString1, "UTF-8"), false);

        byte[] responseString2 = manageClient.get().uri("/shops/0/couponactivities/9")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse2 =  "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"id\": 9,\n" +
                "    \"name\": \"modify\",\n" +
                "    \"beginTime\": \"2020-12-16 08:33:50\",\n" +
                "    \"endTime\": \"2021-12-29 00:28:50\",\n" +
//                "    \"couponTime\": \"2020-12-12T08:28:50\",\n" +
                "    \"state\": 0,\n" +
                "    \"shopId\": 0,\n" +
                "    \"quantity\": 0,\n" +
                "    \"validTerm\": 0,\n" +
                "    \"imageUrl\": null,\n" +
                "    \"strategy\": \"string\",\n" +
                "    \"createdBy\": {\n" +
                "      \"userId\": 1,\n" +
                "      \"userName\": \"13088admin\"\n" +
                "    },\n" +
                "    \"modiBy\": {\n" +
                "      \"userId\": 1,\n" +
                "      \"userName\": \"13088admin\"\n" +
                "    },\n" +
                //               "    \"gmtCreate\": \"2020-12-12T08:33:31\",\n" +
                //               "    \"gmtModified\": \"2020-12-15T08:27:24\",\n" +
                "    \"quantitiyType\": 0\n" +
                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }

    /** 下线优惠活动
     * 异常1
     * 已下线活动不能下线
     * */
    @Order(12)
    @Test
    public void offshelves1() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/0/couponactivities/1/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 904,\n" +
//                "  \"errmsg\": \"优惠活动状态禁止\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /** 下线优惠活动
     * 异常2
     * 已删除活动不能下线
     * */
    @Test
    @Order(13)
    public void offshelves2() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/0/couponactivities/3/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 904,\n" +
//                "  \"errmsg\": \"优惠活动状态禁止\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /** 下线优惠活动
     * 异常3
     * 店铺不属于用户
     * */
    @Test
    @Order(14)
    public void offshelves3() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/2/couponactivities/1/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 505,\n" +
//                "  \"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /** 下线优惠活动
     * 异常4
     * 活动不存在
     * */
    @Test
    @Order(15)
    public void offshelves4() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/0/couponactivities/0/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 504,\n" +
//                "  \"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /** 下线优惠活动
     * 异常5
     * 活动不属于店铺
     * */
    @Test
    @Order(16)
    public void offshelves5() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/0/couponactivities/7/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 505,\n" +
//                "  \"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /** 下线优惠活动
     * 正常
     * 下线成功
     * */
    @Test
    @Order(17)
    public void offshelves6() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/0/couponactivities/2/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
        byte[] responseString2 = mallClient.get().uri("/shops/0/couponactivities/2")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
//        String responseString1 = new String(ret, "UTF-8");
        String expectedResponse2 =  "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"id\": 2,\n" +
                "    \"name\": \"string\",\n" +
                //       "    \"beginTime\": \"2022-12-11T16:02:14\",\n" +
                //         "    \"endTime\": \"2024-12-11T16:02:14\",\n" +
                //           "    \"couponTime\": \"2022-12-12T16:02:14\",\n" +
                "    \"state\": 0,\n" +
                "    \"shopId\": 0,\n" +
                "    \"quantity\": 0,\n" +
                "    \"validTerm\": 0,\n" +
                "    \"imageUrl\": null,\n" +
                "    \"strategy\": \"string\",\n" +
                "    \"createdBy\": {\n" +
                "      \"userId\": 1,\n" +
                "      \"userName\": \"13088admin\"\n" +
                "    },\n" +
                "    \"modiBy\": {\n" +
                "      \"userId\": 1,\n" +
                "      \"userName\": \"13088admin\"\n" +
                "    },\n" +
                //              "    \"gmtCreate\": \"2020-12-13T08:09:52\",\n" +
                //              "    \"gmtModified\": \"2020-12-13T08:14:54\",\n" +
                "    \"quantitiyType\": 0\n" +
                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /** 上线优惠活动
     * 异常1
     * 已上线活动不能上线
     * */
    @Test
    @Order(18)
    public void onshelves1() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/0/couponactivities/5/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 904,\n" +
//                "  \"errmsg\": \"优惠活动状态禁止\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /** 上线优惠活动
     * 异常2
     * 已删除活动不能上线
     * */
    @Test
    @Order(19)
    public void onshelves2() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/0/couponactivities/6/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 904,\n" +
//                "  \"errmsg\": \"优惠活动状态禁止\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /** 上线优惠活动
     * 异常3
     * 店铺不属于用户
     * */
    @Test
    @Order(20)
    public void onshelves3() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/2/couponactivities/1/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse ="{\n" +
                "  \"errno\": 505,\n" +
//                "  \"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /** 上线优惠活动
     * 异常4
     * 活动不存在
     * */
    @Test
    @Order(21)
    public void onshelves4() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/0/couponactivities/0/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 504,\n" +
//                "  \"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /** 上线优惠活动
     * 异常5
     * 活动不属于店铺
     * */
    @Test
    @Order(22)
    public void onshelves5() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/0/couponactivities/7/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 505,\n" +
//                "  \"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /** 上线优惠活动
     * 正常
     * 上线成功
     * */
    @Test
    @Order(23)
    public void onshelves6() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/0/couponactivities/4/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
        byte[] responseString2 = manageClient.get().uri("/shops/0/couponactivities/4")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
//        String responseString1 = new String(ret, "UTF-8");
        String expectedResponse2 =  "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"id\": 4,\n" +
                "    \"name\": \"string\",\n" +
                //       "    \"beginTime\": \"2022-12-11T16:02:14\",\n" +
                //         "    \"endTime\": \"2024-12-11T16:02:14\",\n" +
                //           "    \"couponTime\": \"2022-12-12T16:02:14\",\n" +
                "    \"state\": 1,\n" +
                "    \"shopId\": 0,\n" +
                "    \"quantity\": 0,\n" +
                "    \"validTerm\": 0,\n" +
                "    \"imageUrl\": null,\n" +
                "    \"strategy\": \"string\",\n" +
                "    \"createdBy\": {\n" +
                "      \"userId\": 1,\n" +
                "      \"userName\": \"13088admin\"\n" +
                "    },\n" +
                "    \"modiBy\": {\n" +
                "      \"userId\": 1,\n" +
                "      \"userName\": \"13088admin\"\n" +
                "    },\n" +
                //              "    \"gmtCreate\": \"2020-12-13T08:09:52\",\n" +
                //              "    \"gmtModified\": \"2020-12-13T08:14:54\",\n" +
                "    \"quantitiyType\": 0\n" +
                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /** 删除优惠活动
     * 异常1
     * 已上线活动不能删除
     * */
    @Test
    @Order(24)
    public void delete1() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/0/couponactivities/8")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse =  "{\n" +
                "  \"errno\": 904,\n" +
//                "  \"errmsg\": \"优惠活动状态禁止\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /** 删除优惠活动
     * 异常2
     * 已删除活动不能删除
     * */
    @Test
    @Order(25)
    public void delete2() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/0/couponactivities/3")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse =  "{\n" +
                "  \"errno\": 904,\n" +
//                "  \"errmsg\": \"优惠活动状态禁止\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /** 删除优惠活动
     * 异常3
     * 店铺不属于用户
     * */
    @Test
    @Order(26)
    public void delete3() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/2/couponactivities/1")
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 505,\n" +
//                "  \"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /** 删除优惠活动
     * 异常4
     * 活动不存在
     * */
    @Test
    @Order(27)
    public void delete4() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/0/couponactivities/0")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse= "{\n" +
                "  \"errno\": 504,\n" +
//                "  \"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /** 删除优惠活动
     * 异常5
     * 活动不属于店铺
     * */
    @Test
    @Order(28)
    public void delete5() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/0/couponactivities/7")
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 505,\n" +
//                "  \"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /** 删除优惠活动
     * 正常
     * 成功
     * */
    @Test
    @Order(29)
    public void delete6() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/0/couponactivities/1")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
        byte[] responseString2 = manageClient.get().uri("/shops/0/couponactivities/1")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
//        String responseString1 = new String(ret, "UTF-8");
        String expectedResponse2 =  "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"string\",\n" +
                //       "    \"beginTime\": \"2022-12-11T16:02:14\",\n" +
                //         "    \"endTime\": \"2024-12-11T16:02:14\",\n" +
                //           "    \"couponTime\": \"2022-12-12T16:02:14\",\n" +
                "    \"state\": 2,\n" +
                "    \"shopId\": 0,\n" +
                "    \"quantity\": 0,\n" +
                "    \"validTerm\": 0,\n" +
                "    \"imageUrl\": null,\n" +
                "    \"strategy\": \"string\",\n" +
                "    \"createdBy\": {\n" +
                "      \"userId\": 1,\n" +
                "      \"userName\": \"13088admin\"\n" +
                "    },\n" +
                "    \"modiBy\": {\n" +
                "      \"userId\": 1,\n" +
                "      \"userName\": \"13088admin\"\n" +
                "    },\n" +
                //              "    \"gmtCreate\": \"2020-12-13T08:09:52\",\n" +
                //              "    \"gmtModified\": \"2020-12-13T08:14:54\",\n" +
                "    \"quantitiyType\": 0\n" +
                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /**
     * 查看优惠活动详情
     * 活动不存在
     * */
    @Test
    @Order(30)
    public void getdetail1() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        byte[] queryResponseString = manageClient.get().uri("/shops/0/couponactivities/0")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "  \"errno\": 504,\n" +
//                "  \"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse1,new String(queryResponseString, "UTF-8"), false);
    }
    /**
     * 查看优惠活动详情
     * 不属于该店铺
     * */
    @Test
    @Order(31)
    public void getdetail2() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        byte[] queryResponseString = manageClient.get().uri("/shops/0/couponactivities/7")
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "  \"errno\": 505,\n" +
//                "  \"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse1,new String(queryResponseString, "UTF-8"), false);
    }
    /**
     * 查看优惠活动详情
     * 正常
     * */
    @Test
    @Order(32)
    public void getdetail3() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        byte[] queryResponseString = manageClient.get().uri("/shops/0/couponactivities/6")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"id\": 6,\n" +
                "    \"name\": \"string\",\n" +
                //     "    \"beginTime\": \"2022-12-10T00:02:14\",\n" +
                //      "    \"endTime\": \"2024-12-10T00:02:14\",\n" +
                //       "    \"couponTime\": \"2022-12-10T00:02:14\",\n" +
                "    \"state\": 2,\n" +
                "    \"shopId\": 0,\n" +
                "    \"quantity\": 0,\n" +
                "    \"validTerm\": 0,\n" +
                "    \"imageUrl\": null,\n" +
                "    \"strategy\": \"string\",\n" +
                "    \"createdBy\": {\n" +
                "      \"userId\": 1,\n" +
                "      \"userName\": \"13088admin\"\n" +
                "    },\n" +
                "    \"modiBy\": {\n" +
                "      \"userId\": 1,\n" +
                "      \"userName\": \"13088admin\"\n" +
                "    },\n" +
                //          "    \"gmtCreate\": \"2020-12-10T16:09:54\",\n" +
                //          "    \"gmtModified\": \"2020-12-13T00:50:57\",\n" +
                "    \"quantitiyType\": 0\n" +
                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse1,new String(queryResponseString, "UTF-8"), false);
    }

}

