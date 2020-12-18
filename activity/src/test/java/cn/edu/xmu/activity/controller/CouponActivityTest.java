package cn.edu.xmu.activity.controller;
import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * SPU公开测试
 * @author 24320182203254 秦楚彦
 * @date 2020-12-14
 */
@SpringBootTest(classes = Application.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class CouponActivityTest {
//    @Value("${public-test.managementgate}")
//    private String managementGate;
//    @Value("${public-test.mallgate}")
//    private String mallGate;
    private String managementGate = "127.0.0.1:8092";
    private String mallGate = "127.0.0.1:8092";

    private WebTestClient manageClient;
    private WebTestClient mallClient;
    private String token;

    @BeforeEach
    public void setUp(){
        token = creatTestToken(1L,2L,100);
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

    /**
     * description: 创建测试用token
     */
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        return token;
    }
    /** 查看本店已下线优惠活动
     * 异常1
     * 店铺不属于用户
     * */
    @Test
    @Order(2)
    public void getunderline1() throws Exception {
        token = creatTestToken(1L,3L,100);
        byte[] queryResponseString = manageClient.get().uri("/shops/2/couponactivities/invalid").header("authorization",token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "  \"errno\": 505\n" +
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
        token = creatTestToken(1L,0L,100);
        byte[] queryResponseString = manageClient.get().uri("/shops/0/couponactivities/invalid").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 4,\n" +
                "    \"pages\": 1,\n" +
//                "    \"pageSize\": 10,\n" +
//                "    \"page\": 1,\n" +
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
                "      },\n" +
                "      {\n" +
                "        \"id\": 2158,\n" +
                "        \"name\": \"双十一\",\n" +
                "        \"beginTime\": \"2021-01-20 22:46:38\",\n" +
                "        \"endTime\": \"2021-01-30 22:46:55\",\n" +
                "        \"couponTime\": \"2021-01-10 22:47:01\",\n" +
                "        \"quantity\": 0,\n" +
                "        \"imageUrl\": null\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
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
        token = creatTestToken(1L,0L,100);
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
                "    \"total\": 4,\n" +
                "    \"pages\": 4,\n" +
//                "    \"pageSize\": 1,\n" +
//                "    \"page\": 2,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 4,\n" +
                "        \"name\": \"string\",\n" +
                "        \"beginTime\": \"2022-12-13 08:02:14\",\n" +
                "        \"endTime\": \"2024-12-13 08:02:14\",\n" +
                "        \"couponTime\": \"2022-12-13 08:02:14\",\n" +
                "        \"quantity\": 0,\n" +
                "        \"imageUrl\": null\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
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
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 0,\n" +
                "    \"pages\": 0,\n" +
//                "    \"pageSize\": 10,\n" +
//                "    \"page\": 1,\n" +
                "    \"list\": []\n" +
                "  }\n" +
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
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 3,\n" +
                "    \"pages\": 1,\n" +
                "    \"pageSize\": 10,\n" +
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
                "      },\n" +
                "      {\n" +
                "        \"id\": 20001,\n" +
                "        \"name\": \"cpz1\",\n" +
                "        \"beginTime\": \"2020-11-01 03:22:30\",\n" +
                "        \"endTime\": \"2021-01-31 03:22:39\",\n" +
                "        \"couponTime\": \"2020-12-01 03:22:52\",\n" +
                "        \"quantity\": 100,\n" +
                "        \"imageUrl\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 20002,\n" +
                "        \"name\": \"cpz2\",\n" +
                "        \"beginTime\": \"2020-11-01 03:31:24\",\n" +
                "        \"endTime\": \"2021-01-31 03:31:38\",\n" +
                "        \"couponTime\": \"2020-12-01 03:31:44\",\n" +
                "        \"quantity\": 200,\n" +
                "        \"imageUrl\": null\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
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
        token = creatTestToken(1L,1L,100);
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
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\n" +
                "  \"errno\": 505\n" +
//                "  \"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString1, "UTF-8"), false);
    }

    /**
     * 成功
     **/
    @Test
    @Order(8)
    public void createCouponActivity2() throws Exception {
        token = creatTestToken(1L,0L,100);
        String json="{\n" +
                "  \"beginTime\": \"2021-12-17T08:39:13.741Z\",\n" +
                "  \"couponTime\": \"2021-12-17T08:39:13.741Z\",\n" +
                "  \"endTime\": \"2022-12-17T08:39:13.741Z\",\n" +
                "  \"name\": \"string\",\n" +
                "  \"quantitiyType\": 0,\n" +
                "  \"quantity\": 0,\n" +
                "  \"strategy\": \"string\",\n" +
                "  \"validTerm\": 0\n" +
                "}";
        byte[] responseString = manageClient.post().uri("/shops/0/couponactivities")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse =   "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"id\": 20007,\n" +
                "    \"name\": \"string\",\n" +
                "    \"beginTime\": \"2021-12-17 08:39:13\",\n" +
                "    \"endTime\": \"2022-12-17 08:39:13\",\n" +
                "    \"couponTime\": \"2021-12-17 08:39:13\",\n" +
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
//                "    \"gmtCreate\": \"2020-12-17 16:51:24\",\n" +
                "    \"gmtModified\": null,\n" +
                "    \"quantitiyType\": 0\n" +
                "  }\n" +
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
        token = creatTestToken(1L,1L,100);
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
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse =  "{\n" +
                "  \"errno\": 505\n" +
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
        token = creatTestToken(1L,0L,100);
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
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse =  "{\n" +
                "  \"errno\": 505\n" +
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
        token = creatTestToken(1L,0L,100);
        String json="{\n" +
                "  \"beginTime\": \"2020-12-31T16:33:50\",\n" +
                "  \"endTime\": \"2021-12-31T21:28:50\",\n" +
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
                "  \"errno\": 0\n" +
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
                "    \"beginTime\": \"2020-12-31 08:33:50\",\n" +
                "    \"endTime\": \"2021-12-31 13:28:50\",\n" +
//                "    \"couponTime\": \"2020-12-14 00:28:50\",\n" +
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
//                "    \"gmtCreate\": \"2020-12-13 16:33:31\",\n" +
//                "    \"gmtModified\": \"2020-12-17 09:33:08\",\n" +
                "    \"quantitiyType\": 0\n" +
                "  }\n" +
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
        token = creatTestToken(1L,1L,100);
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
                "  \"errno\": 904\n" +
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
        token = creatTestToken(1L,0L,100);
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
                "  \"errno\": 904\n" +
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
        token = creatTestToken(1L,1L,100);
        byte[] ret = manageClient.put()
                .uri("/shops/2/couponactivities/1/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 505\n" +
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
        token = creatTestToken(1L,0L,100);
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
                "  \"errno\": 504\n" +
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
        token = creatTestToken(1L,7L,100);
        byte[] ret = manageClient.put()
                .uri("/shops/0/couponactivities/7/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 505\n" +
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
        token = creatTestToken(1L,0L,100);
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
                "  \"errno\": 0\n" +
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
                "  }\n" +
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
        token = creatTestToken(1L,1L,100);
        byte[] ret = manageClient.put()
                .uri("/shops/0/couponactivities/5/onshelves")
                .header("authorization",token)
                .exchange()
                //.expectStatus().isBadRequest()//状态码应该为OK
                .expectStatus().isOk()//状态码应该为OK
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 904\n" +
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
        token = creatTestToken(1L,1L,100);
        byte[] ret = manageClient.put()
                .uri("/shops/0/couponactivities/6/onshelves")
                .header("authorization",token)
                .exchange()
//                .expectStatus().isBadRequest() //状态码应该为OK
                .expectStatus().isOk()//状态码应该为OK
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 904\n" +
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
        token = creatTestToken(1L,1L,100);
        byte[] ret = manageClient.put()
                .uri("/shops/2/couponactivities/1/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse ="{\n" +
                "  \"errno\": 505\n" +
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
        token = creatTestToken(1L,0L,100);
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
                "  \"errno\": 504\n" +
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
        token = creatTestToken(1L,1L,100);
        byte[] ret = manageClient.put()
                .uri("/shops/0/couponactivities/7/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 505\n" +
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
        token = creatTestToken(1L,0L,100);
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
                "  \"errno\": 0\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
//        byte[] responseString2 = manageClient.get().uri("/shops/0/couponactivities/4")
//                .header("authorization",token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .returnResult()
//                .getResponseBodyContent();
//        String expectedResponse2 =  "{\n" +
//                "  \"errno\": 0,\n" +
//                "  \"data\": {\n" +
//                "    \"id\": 4,\n" +
//                "    \"name\": \"string\",\n" +
//                //       "    \"beginTime\": \"2022-12-11T16:02:14\",\n" +
//                //         "    \"endTime\": \"2024-12-11T16:02:14\",\n" +
//                //           "    \"couponTime\": \"2022-12-12T16:02:14\",\n" +
//                "    \"state\": 1,\n" +
//                "    \"shopId\": 0,\n" +
//                "    \"quantity\": 0,\n" +
//                "    \"validTerm\": 0,\n" +
//                "    \"imageUrl\": null,\n" +
//                "    \"strategy\": \"string\",\n" +
//                "    \"createdBy\": {\n" +
//                "      \"userId\": 1,\n" +
//                "      \"userName\": \"13088admin\"\n" +
//                "    },\n" +
//                "    \"modiBy\": {\n" +
//                "      \"userId\": 1,\n" +
//                "      \"userName\": \"13088admin\"\n" +
//                "    },\n" +
//                //              "    \"gmtCreate\": \"2020-12-13T08:09:52\",\n" +
//                //              "    \"gmtModified\": \"2020-12-13T08:14:54\",\n" +
//                "    \"quantitiyType\": 0\n" +
//                "  }\n" +
////                "  \"errmsg\": \"成功\"\n" +
//                "}";
//        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /** 删除优惠活动
     * 异常1
     * 已上线活动不能删除
     * */
    @Test
    @Order(24)
    public void delete1() throws Exception {
        token = creatTestToken(1L,0L,100);
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
                "  \"errno\": 904\n" +
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
        token = creatTestToken(1L,0L,100);
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
                "  \"errno\": 904\n" +
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
        token = creatTestToken(1L,1L,100);
        byte[] ret = manageClient.delete()
                .uri("/shops/2/couponactivities/1")
                .header("authorization",token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 505\n" +
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
        token = creatTestToken(1L,0L,100);
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
                "  \"errno\": 504\n" +
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
        token = creatTestToken(1L,1L,100);
        byte[] ret = manageClient.delete()
                .uri("/shops/0/couponactivities/7")
                .header("authorization",token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 505\n" +
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
        token = creatTestToken(1L,0L,100);
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
                "  \"errno\": 0\n" +
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
                "  }\n" +
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

        byte[] queryResponseString = manageClient.get().uri("/shops/0/couponactivities/0")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "  \"errno\": 504\n" +
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
        token = creatTestToken(1L,0L,100);
        byte[] queryResponseString = manageClient.get().uri("/shops/0/couponactivities/7")
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "  \"errno\": 505\n" +
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
        token = creatTestToken(1L,0L,100);
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
                "  }\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse1,new String(queryResponseString, "UTF-8"), false);
    }

}


