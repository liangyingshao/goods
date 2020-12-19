package cn.edu.xmu.activity.controller;

import cn.edu.xmu.activity.ActivityServiceApplication;
import cn.edu.xmu.activity.LoginVo;
import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

/**
 * @Author Pinzhen Chen 24320182203173
 * @Date 2020/12/13 20:40
 */
@SpringBootTest(classes = Application.class)
public class ChenPinzhenTest {


    //@Value("${public-test.managementgate}")
    private String managementGate;

    //@Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    @BeforeEach
    public void setUp(){

        managementGate="192.168.43.194:8881";
        mallGate="192.168.43.194:8880";

        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://"+managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://"+mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

    }

    private String login(String userName, String password) throws Exception {
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);
        byte[] ret = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return JacksonUtil.parseString(new String(ret, "UTF-8"), "data");
        //endregion
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
     * 查看一条spu详细信息（无需登录）
     *
     * @author 24320182203173 Chen Pinzhen
     * @date: 2020/12/13 20:44
     */

    //200: 成功
    @Test
    public void getSpuByIdTest1() throws Exception {
        byte[] ret = mallClient.get()
                .uri("/spus/20680")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":20680,\"name\":\"cpz1\",\"brand\":{\"id\":20120,\"name\":\"cpz1\",\"imageUrl\":null},\"category\":{\"id\":20140,\"name\":\"cpz1\"},\"freight\":null,\"shop\":{\"id\":0,\"name\":null},\"goodsSn\":\"drh-d0001\",\"detail\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"spec\":null,\"gmtCreate\":\"2020-12-08T14:36:01\",\"gmtModified\":\"2020-12-08T14:36:01\",\"disable\":0,\"skuList\":[{\"id\":20680,\"skuSn\":null,\"name\":\"cpz1\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disable\":false,\"price\":980000}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    //404: spuId不存在
    @Test
    public void getSpuByIdTest2() throws Exception {
        byte[] ret = mallClient.get()
                .uri("/spus/2")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }


    /**
     * 店家新建商品SPU
     *
     * @author 24320182203173 Chen Pinzhen
     * @date: 2020/12/13 21:21
     */
    //200: 成功
    @Test
    public void insertSpu1() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        String json = "{\n" +
                "  \"decription\": \"string\",\n" +
                "  \"name\": \"string\",\n" +
                "  \"specs\": \"string\"\n" +
                "}";
        byte[] ret = manageClient.post()
                .uri("/shops/20001/spus")
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"name\": \"string\",\n" +
                "    \"brand\": null,\n" +
                "    \"category\": null,\n" +
                "    \"freight\": null,\n" +
                "    \"shop\": null,\n" +
                "    \"goodsSn\": null,\n" +
                "    \"detail\": \"string\",\n" +
                "    \"imageUrl\": null,\n" +
                "    \"spec\": null,\n" +
                "    \"disable\": null,\n" +
                "    \"skuList\": null\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
        int startIndex = responseString.indexOf("id");
        int endIndex = responseString.indexOf("name");
        String id = responseString.substring(startIndex, endIndex);
        byte[] queryResponseString = manageClient.get().uri("/spus/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\"errno\":0,\"data\":{\"name\":\"string\",\"brand\":null,\"category\":null,\"freight\":null,\"shop\":{\"id\":20001,\"name\":\"cpz1\"},\"goodsSn\":null,\"detail\":\"string\",\"imageUrl\":null,\"spec\":null,\"disable\":null,\"skuList\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse1, new String(queryResponseString, "UTF-8"), false);
    }

    //503: departId与shopId不匹配
    @Test
    public void insertSpu2() throws Exception {
        String token = this.adminLogin("537300010", "123456");
        String json = "{\n" +
                "  \"decription\": \"test\",\n" +
                "  \"name\": \"test\",\n" +
                "  \"specs\": \"test\"\n" +
                "}";
        byte[] ret = manageClient.post()
                .uri("/shops/1/spus")
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 505,\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }


    /**
     * 店家修改商品SPU
     *
     * @author 24320182203173 Chen Pinzhen
     * @date: 2020/12/13 23:27
     */
    //200: 成功
    @Test
    public void updateSpu1() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        String json = "{\n" +
                "  \"decription\": \"string\",\n" +
                "  \"name\": \"string\",\n" +
                "  \"specs\": \"string\"\n" +
                "}";
        byte[] ret = manageClient.put()
                .uri("/shops/0/spus/20683")
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 0\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
        byte[] queryResponseString = manageClient.get().uri("/spus/20683")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"id\": 20683,\n" +
                "        \"name\": \"string\",\n" +
                "        \"brand\": {\n" +
                "            \"id\": 20121,\n" +
                "            \"name\": \"cpz2\",\n" +
                "            \"imageUrl\": null\n" +
                "        },\n" +
                "        \"category\": null,\n" +
                "        \"freight\": null,\n" +
                "        \"shop\": {\n" +
                "            \"id\": 0,\n" +
                "            \"name\": null\n" +
                "        },\n" +
                "        \"goodsSn\": \"bcl-b0001\",\n" +
                "        \"detail\": \"string\",\n" +
                "        \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_5861cd259e57a.jpg\",\n" +
                "        \"spec\": null,\n" +
                "        \"disable\": 0,\n" +
                "        \"skuList\": []\n" +
                "    }" +
                "}";
        JSONAssert.assertEquals(expectedResponse1, new String(queryResponseString, "UTF-8"), false);
    }


    //404: spuId不存在
    @Test
    public void updateSpu2() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        byte[] ret = manageClient.get()
                .uri("/spus/2")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }


    /**
     * 逻辑删除spu
     *
     * @author 24320182203173 Chen Pinzhen
     * @date: 2020/12/13 23:50
     */

    //200: 成功
    @Test
    public void deleteSpu1() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/0/spus/20680")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 0\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
        byte[] queryResponseString = manageClient.get().uri("/spus/20680")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "  \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse1, new String(queryResponseString, "UTF-8"), false);
    }

    //504: 重复删除spu
    @Test
    public void deleteSpu2() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/0/spus/20681")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":503}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 店家商品上架
     *
     * @author 24320182203173 Chen Pinzhen
     * @date: 2020/12/14 9:12
     */

    //200: 成功
    @Test
    public void onshelfSku1() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        byte[] ret = manageClient.put()
                .uri("/shops/0/skus/20680/onshelves")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 0\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
        int startIndex = responseString.indexOf("id");
        int endIndex = responseString.indexOf("name");
        String id = responseString.substring(startIndex, endIndex);
        byte[] queryResponseString = manageClient.get().uri("/skus/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"id\": 20680,\n" +
                "    \"skuSn\": null,\n" +
                "    \"name\": \"test\",\n" +
                "    \"detail\": null,\n" +
                "    \"originalPrice\": 22,\n" +
                "    \"imageUrl\": \"http://47.52.88.176/file/images/201912/file_5df048b5be168.jpg\",\n" +
                "    \"inventory\": 300,\n" +
                "    \"disable\": false,\n" +
                "    \"price\": 22,\n" +
                "    \"configuration\": null,\n" +
                "    \"weight\": 3,\n" +
                "    \"gmtCreate\": \"2020-12-14T01:21:47\",\n" +
                "    \"gmtModified\": null,\n" +
                "    \"spu\": {\n" +
                "      \"id\": 20680,\n" +
                "      \"name\": \"金和汇景•戴荣华•古彩洛神赋瓷瓶\",\n" +
                "      \"brand\": null,\n" +
                "      \"category\": null,\n" +
                "      \"freight\": null,\n" +
                "      \"shop\": null,\n" +
                "      \"goodsSn\": \"drh-d0001\",\n" +
                "      \"detail\": null,\n" +
                "      \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\n" +
                "      \"state\": null,\n" +
                "      \"spec\": null,\n" +
                "      \"gmtCreate\": \"2020-12-10T22:36:01\",\n" +
                "      \"gmtModified\": \"2020-12-10T22:36:01\",\n" +
                "      \"disable\": 0,\n" +
                "      \"skuList\": null\n" +
                "    }\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"";
        JSONAssert.assertEquals(expectedResponse1, new String(queryResponseString, "UTF-8"), false);
    }

    //504: sku状态不合格
    @Test
    public void onshelfSku2() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        byte[] ret = manageClient.put()
                .uri("/shops/0/skus/273")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
        int startIndex = responseString.indexOf("id");
        int endIndex = responseString.indexOf("name");
        String id = responseString.substring(startIndex, endIndex);
        byte[] queryResponseString = manageClient.get().uri("/skus/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"id\": 20680,\n" +
                "    \"skuSn\": null,\n" +
                "    \"name\": \"+\",\n" +
                "    \"detail\": null,\n" +
                "    \"originalPrice\": 980000,\n" +
                "    \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\n" +
                "    \"inventory\": 1,\n" +
                "    \"disable\": false,\n" +
                "    \"price\": 980000,\n" +
                "    \"configuration\": null,\n" +
                "    \"weight\": 10,\n" +
                "    \"gmtCreate\": \"2020-12-10T22:36:00\",\n" +
                "    \"gmtModified\": \"2020-12-10T22:36:00\",\n" +
                "    \"spu\": {\n" +
                "      \"id\": 273,\n" +
                "      \"name\": \"金和汇景•戴荣华•古彩洛神赋瓷瓶\",\n" +
                "      \"brand\": null,\n" +
                "      \"category\": null,\n" +
                "      \"freight\": null,\n" +
                "      \"shop\": null,\n" +
                "      \"goodsSn\": \"drh-d0001\",\n" +
                "      \"detail\": null,\n" +
                "      \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\n" +
                "      \"state\": null,\n" +
                "      \"spec\": null,\n" +
                "      \"gmtCreate\": \"2020-12-10T22:36:01\",\n" +
                "      \"gmtModified\": \"2020-12-10T22:36:01\",\n" +
                "      \"disable\": 0,\n" +
                "      \"skuList\": null\n" +
                "    }\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse1, new String(queryResponseString, "UTF-8"), false);
    }

    /**
     * 店家商品下架
     *
     * @author 24320182203173 Chen Pinzhen
     * @date: 2020/12/14 9:25
     */
    //200: 成功
    @Test
    public void offshelfSku1() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        byte[] ret = manageClient.put()
                .uri("/shops/0/skus/20680")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
        int startIndex = responseString.indexOf("id");
        int endIndex = responseString.indexOf("name");
        String id = responseString.substring(startIndex, endIndex);
        byte[] queryResponseString = manageClient.get().uri("/skus/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"id\": 20680,\n" +
                "    \"skuSn\": null,\n" +
                "    \"name\": \"+\",\n" +
                "    \"detail\": null,\n" +
                "    \"originalPrice\": 980000,\n" +
                "    \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\n" +
                "    \"inventory\": 1,\n" +
                "    \"disable\": false,\n" +
                "    \"price\": 980000,\n" +
                "    \"configuration\": null,\n" +
                "    \"weight\": 10,\n" +
                "    \"gmtCreate\": \"2020-12-10T22:36:00\",\n" +
                "    \"gmtModified\": \"2020-12-10T22:36:00\",\n" +
                "    \"spu\": {\n" +
                "      \"id\": 20680,\n" +
                "      \"name\": \"金和汇景•戴荣华•古彩洛神赋瓷瓶\",\n" +
                "      \"brand\": null,\n" +
                "      \"category\": null,\n" +
                "      \"freight\": null,\n" +
                "      \"shop\": null,\n" +
                "      \"goodsSn\": \"drh-d0001\",\n" +
                "      \"detail\": null,\n" +
                "      \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\n" +
                "      \"state\": null,\n" +
                "      \"spec\": null,\n" +
                "      \"gmtCreate\": \"2020-12-10T22:36:01\",\n" +
                "      \"gmtModified\": \"2020-12-10T22:36:01\",\n" +
                "      \"disable\": 0,\n" +
                "      \"skuList\": null\n" +
                "    }\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse1, new String(queryResponseString, "UTF-8"), false);
    }

    //504: sku状态不合格
    @Test
    public void offshelfSku2() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        byte[] ret = manageClient.put()
                .uri("/shops/0/skus/20680")
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
        int startIndex = responseString.indexOf("id");
        int endIndex = responseString.indexOf("name");
        String id = responseString.substring(startIndex, endIndex);
        byte[] queryResponseString = manageClient.get().uri("/skus/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"id\": 20680,\n" +
                "    \"skuSn\": null,\n" +
                "    \"name\": \"test\",\n" +
                "    \"detail\": null,\n" +
                "    \"originalPrice\": 22,\n" +
                "    \"imageUrl\": \"http://47.52.88.176/file/images/201912/file_5df048b5be168.jpg\",\n" +
                "    \"inventory\": 300,\n" +
                "    \"disable\": false,\n" +
                "    \"price\": 22,\n" +
                "    \"configuration\": null,\n" +
                "    \"weight\": 3,\n" +
                "    \"gmtCreate\": \"2020-12-14T01:21:47\",\n" +
                "    \"gmtModified\": null,\n" +
                "    \"spu\": {\n" +
                "      \"id\": 20680,\n" +
                "      \"name\": \"金和汇景•戴荣华•古彩洛神赋瓷瓶\",\n" +
                "      \"brand\": null,\n" +
                "      \"category\": null,\n" +
                "      \"freight\": null,\n" +
                "      \"shop\": null,\n" +
                "      \"goodsSn\": \"drh-d0001\",\n" +
                "      \"detail\": null,\n" +
                "      \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\n" +
                "      \"state\": null,\n" +
                "      \"spec\": null,\n" +
                "      \"gmtCreate\": \"2020-12-10T22:36:01\",\n" +
                "      \"gmtModified\": \"2020-12-10T22:36:01\",\n" +
                "      \"disable\": 0,\n" +
                "      \"skuList\": null\n" +
                "    }\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"";
        JSONAssert.assertEquals(expectedResponse1, new String(queryResponseString, "UTF-8"), false);
    }


    /**
     * 管理员新增商品价格浮动
     *
     * @author 24320182203173 Chen Pinzhen
     * @date: 2020/12/14 9:35
     */

    //200: 成功
    @Test
    public void insertFloatPrice1() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        String json = "{\n" +
                "  \"activityPrice\": 10,\n" +
                "  \"beginTime\": \"2021-12-14 03:00:52\",\n" +
                "  \"endTime\": \"2021-12-24 03:00:52\",\n" +
                "  \"quantity\": 10\n" +
                "}";
        byte[] ret = manageClient.post()
                .uri("/shops/0/skus/273/floatPrices")
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 0\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }


    //902: 浮动价格时间冲突
    @Test
    public void insertFloatPrice2() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        String json = "{\n" +
                "  \"activityPrice\": 10,\n" +
                "  \"beginTime\": \"2020-11-14 02:23:01\",\n" +
                "  \"endTime\": \"2020-12-14 02:23:01\",\n" +
                "  \"quantity\": 10\n" +
                "}";
        byte[] ret = manageClient.post()
                .uri("/shops/0/skus/273/floatPrices")
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.SKUPRICE_CONFLICT.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 902\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }


    //404: skuId不存在
    @Test
    public void insertFloatPrice3() throws Exception {
        byte[] ret = manageClient.get()
                .uri("/spus/2")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }


    /**
     * 管理员失效商品价格浮动
     *
     * @author 24320182203173 Chen Pinzhen
     * @date: 2020/12/14 9:35
     */

    //200: 成功
    @Test
    public void deleteFloatPrice1() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/0/floatPrices/20001")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 0\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

//
//    //504: 浮动价格已经失效
//    @Test
//    public void deleteFloatPrice2() throws Exception {
//        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
//        byte[] ret = manageClient.delete()
//                .uri("/shops/0/floatPrices/20004")
//                .header("authorization", token)
//                .exchange()
//                .expectStatus().isForbidden()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
//                .returnResult()
//                .getResponseBodyContent();
//        String responseString = new String(ret, "UTF-8");
//        String expectedResponse = "{\n" +
//                "  \"errno\": 504,\n" +
//                "  \"errmsg\": \"操作的资源id不存在\"\n" +
//                "}";
//        JSONAssert.assertEquals(expectedResponse, responseString, false);
//    }


    //404: skuId不存在或浮动价格不存在
    @Test
    public void deleteFloatPrice3() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/0/floatPrices/20010")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":504\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }


    /**
     * 将spu加入品牌
     *
     * @author 24320182203173 Chen Pinzhen
     * @date: 2020/12/15 1:44
     */

    //200: 成功
    @Test
    public void insertSpuToBrand1() throws Exception {
        String token = this.adminLogin("537300010", "123456");
        byte[] ret = manageClient.post()
                .uri("/shops/0/spus/20680/brands/20121")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 0\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
        byte[] queryResponseString = manageClient.get().uri("/spus/20680")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"id\": 20680,\n" +
                "        \"name\": \"cpz1\",\n" +
                "        \"brand\": {\n" +
                "            \"id\": 20121,\n" +
                "            \"name\": \"cpz2\",\n" +
                "            \"imageUrl\": null\n" +
                "        },\n" +
                "        \"category\": {\n" +
                "            \"id\": 20141,\n" +
                "            \"name\": \"cpz2\"\n" +
                "        },\n" +
                "        \"freight\": null,\n" +
                "        \"shop\": {\n" +
                "            \"id\": 0,\n" +
                "            \"name\": null\n" +
                "        },\n" +
                "        \"goodsSn\": \"drh-d0001\",\n" +
                "        \"detail\": null,\n" +
                "        \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\n" +
                "        \"spec\": null,\n" +
                "        \"disable\": 0,\n" +
                "        \"skuList\": [\n" +
                "            {\n" +
                "                \"id\": 20680,\n" +
                "                \"skuSn\": null,\n" +
                "                \"name\": \"cpz1\",\n" +
                "                \"originalPrice\": 980000,\n" +
                "                \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\n" +
                "                \"inventory\": 1,\n" +
                "                \"disable\": false,\n" +
                "                \"price\": 980000\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse1, new String(queryResponseString, "UTF-8"), false);
    }

    //404: 品牌不存在
    @Test
    public void insertSpuToBrand2() throws Exception {
        String token = this.adminLogin("537300010", "123456");
        byte[] ret = manageClient.post()
                .uri("/shops/0/spus/20680/brands/100001")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
        byte[] queryResponseString = manageClient.get().uri("/spus/20680")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"id\": 20680,\n" +
                "        \"name\": \"cpz1\",\n" +
                "        \"brand\": {\n" +
                "            \"id\": 20121,\n" +
                "            \"name\": \"cpz2\",\n" +
                "            \"imageUrl\": null\n" +
                "        },\n" +
                "        \"category\": {\n" +
                "            \"id\": 20141,\n" +
                "            \"name\": \"cpz2\"\n" +
                "        },\n" +
                "        \"freight\": null,\n" +
                "        \"shop\": {\n" +
                "            \"id\": 0,\n" +
                "            \"name\": null\n" +
                "        },\n" +
                "        \"goodsSn\": \"drh-d0001\",\n" +
                "        \"detail\": null,\n" +
                "        \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\n" +
                "        \"spec\": null,\n" +
                "        \"disable\": 0,\n" +
                "        \"skuList\": [\n" +
                "            {\n" +
                "                \"id\": 20680,\n" +
                "                \"skuSn\": null,\n" +
                "                \"name\": \"cpz1\",\n" +
                "                \"originalPrice\": 980000,\n" +
                "                \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\n" +
                "                \"inventory\": 1,\n" +
                "                \"disable\": false,\n" +
                "                \"price\": 980000\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse1, new String(queryResponseString, "UTF-8"), false);
    }


    /**
     * 将spu移出品牌
     *
     * @author 24320182203173 Chen Pinzhen
     * @date: 2020/12/15 1:44
     */

    //200: 成功
    @Test
    public void deleteSpuFromBrand1() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/0/spus/20680/brands/20120")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 0\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
        byte[] queryResponseString = manageClient.get().uri("/spus/20680")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\"errno\":0,\"data\":{\"id\":20680,\"name\":\"cpz1\",\"brand\":null,\"category\":{\"id\":20140,\"name\":\"cpz1\"},\"freight\":null,\"shop\":{\"id\":0,\"name\":null},\"goodsSn\":\"drh-d0001\",\"detail\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"state\":null,\"spec\":null,\"gmtCreate\":\"2020-12-09T06:36:01\",\"gmtModified\":\"2020-12-09T06:36:01\",\"disable\":0,\"skuList\":[{\"id\":20680,\"skuSn\":null,\"name\":\"cpz1\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disable\":false,\"price\":980000}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse1, new String(queryResponseString, "UTF-8"), true);
    }

    //404: 品牌不存在
    @Test
    public void deleteSpuFromBrand2() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/0/spus/20680/brands/20130")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 504,\n" +
                "  \"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
        byte[] queryResponseString = manageClient.get().uri("/spus/20680")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\"errno\":0,\"data\":{\"id\":20680,\"name\":\"cpz1\",\"brand\":{\"id\":20120,\"name\":\"cpz1\",\"imageUrl\":null},\"category\":{\"id\":20140,\"name\":\"cpz1\"},\"freight\":null,\"shop\":{\"id\":0,\"name\":null},\"goodsSn\":\"drh-d0001\",\"detail\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"state\":null,\"spec\":null,\"gmtCreate\":\"2020-12-09T06:36:01\",\"gmtModified\":\"2020-12-09T06:36:01\",\"disable\":0,\"skuList\":[{\"id\":680,\"skuSn\":null,\"name\":\"cpz1\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disable\":false,\"price\":980000}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse1, new String(queryResponseString, "UTF-8"), true);
    }


    /**
     * 将spu加入分类
     *
     * @author 24320182203173 Chen Pinzhen
     * @date: 2020/12/15 1:44
     */

    //200: 成功
    @Test
    public void insertSpuToCategory1() throws Exception {
        String token = this.adminLogin("537300010", "123456");
        byte[] ret = manageClient.post()
                .uri("/shops/0/spus/20680/categories/20141")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 0\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
        byte[] queryResponseString = manageClient.get().uri("/spus/20680")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"id\": 20680,\n" +
                "        \"name\": \"cpz1\",\n" +
                "        \"brand\": {\n" +
                "            \"id\": 20121,\n" +
                "            \"name\": \"cpz2\",\n" +
                "            \"imageUrl\": null\n" +
                "        },\n" +
                "        \"category\": {\n" +
                "            \"id\": 20141,\n" +
                "            \"name\": \"cpz2\"\n" +
                "        },\n" +
                "        \"freight\": null,\n" +
                "        \"shop\": {\n" +
                "            \"id\": 0,\n" +
                "            \"name\": null\n" +
                "        },\n" +
                "        \"goodsSn\": \"drh-d0001\",\n" +
                "        \"detail\": null,\n" +
                "        \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\n" +
                "        \"spec\": null,\n" +
                "        \"disable\": 0,\n" +
                "        \"skuList\": [\n" +
                "            {\n" +
                "                \"id\": 20680,\n" +
                "                \"skuSn\": null,\n" +
                "                \"name\": \"cpz1\",\n" +
                "                \"originalPrice\": 980000,\n" +
                "                \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\n" +
                "                \"inventory\": 1,\n" +
                "                \"disable\": false,\n" +
                "                \"price\": 980000\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse1, new String(queryResponseString, "UTF-8"), false);
    }

    //404: 分类不存在
    @Test
    public void insertSpuToCategory2() throws Exception {
        String token = this.adminLogin("537300010", "123456");
        byte[] ret = manageClient.post()
                .uri("/shops/0/spus/20680/categories/1001")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
        byte[] queryResponseString = manageClient.get().uri("/spus/20680")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\"errno\":0,\"data\":{\"id\":20680,\"name\":\"cpz1\",\"brand\":{\"id\":20120,\"name\":\"cpz1\",\"imageUrl\":null},\"category\":{\"id\":20140,\"name\":\"cpz1\"},\"freight\":null,\"shop\":{\"id\":0,\"name\":null},\"goodsSn\":\"drh-d0001\",\"detail\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"state\":null,\"spec\":null,\"gmtCreate\":\"2020-12-08T22:36:01\",\"gmtModified\":\"2020-12-08T22:36:01\",\"disable\":0,\"skuList\":[{\"id\":680,\"skuSn\":null,\"name\":\"cpz1\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disable\":false,\"price\":980000}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse1, new String(queryResponseString, "UTF-8"), true);
    }


    /**
     * 将spu移出分类
     *
     * @author 24320182203173 Chen Pinzhen
     * @date: 2020/12/15 1:44
     */

    //200: 成功
    @Test
    public void deleteSpuFromCategory1() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/0/spus/20680/categories/20140")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 0\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
        byte[] queryResponseString = manageClient.get().uri("/spus/20680")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\"errno\":0,\"data\":{\"id\":20680,\"name\":\"cpz1\",\"brand\":{\"id\":20120,\"name\":\"cpz1\",\"imageUrl\":null},\"category\":null,\"freight\":null,\"shop\":{\"id\":0,\"name\":null},\"goodsSn\":\"drh-d0001\",\"detail\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"state\":null,\"spec\":null,\"gmtCreate\":\"2020-12-08T14:36:01\",\"gmtModified\":\"2020-12-08T14:36:01\",\"disable\":0,\"skuList\":[{\"id\":680,\"skuSn\":null,\"name\":\"cpz1\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disable\":false,\"price\":980000}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse1, new String(queryResponseString, "UTF-8"), true);
    }

    //404: 分类不存在
    @Test
    public void deleteSpuFromCategory2() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/0/spus/20680/categories/1001")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
        byte[] queryResponseString = manageClient.get().uri("/spus/20680")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\"errno\":0,\"data\":{\"id\":20680,\"name\":\"cpz1\",\"brand\":{\"id\":20120,\"name\":\"cpz1\",\"imageUrl\":null},\"category\":{\"id\":140,\"name\":\"cpz1\"},\"freight\":null,\"shop\":{\"id\":0,\"name\":null},\"goodsSn\":\"drh-d0001\",\"detail\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"state\":null,\"spec\":null,\"gmtCreate\":\"2020-12-08T22:36:01\",\"gmtModified\":\"2020-12-08T22:36:01\",\"disable\":0,\"skuList\":[{\"id\":680,\"skuSn\":null,\"name\":\"cpz1\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disable\":false,\"price\":980000}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse1, new String(queryResponseString, "UTF-8"), true);
    }


    /**
     * 获得优惠券的所有状态
     *
     * @author 24320182203173 Chen Pinzhen
     * @date: 2020/12/15 1:48
     */
    //200: 成功
    @Test
    public void getAllCouponStates() throws Exception {
        byte[] ret = manageClient.get()
                .uri("/coupons/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"未领取\",\"code\":0},{\"name\":\"已领取\",\"code\":1},{\"name\":\"已使用\",\"code\":2},{\"name\":\"已失效\",\"code\":3}]}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }


    /**
     * 买家查看自己的优惠券列表
     *
     * @author 24320182203173 Chen Pinzhen
     * @date: 2020/12/16 3:19
     */
    //200: 第一页
    @Test
    public void getCouponList1() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        byte[] ret = manageClient.get()
                .uri("/coupons?page=1&pageSize=3&state=4")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 3,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1,\n" +
                "        \"name\": \"cpz1\",\n" +
                "        \"couponSn\": null,\n" +
                "        \"couponActivitySimpleRetVo\": {\n" +
                "          \"id\": 1,\n" +
                "          \"name\": \"cpz1\",\n" +
                "          \"beginTime\": \"2020-11-01T03:22:30\",\n" +
                "          \"endTime\": \"2021-01-31T03:22:39\",\n" +
                "          \"couponTime\": \"2020-12-01T03:22:52\",\n" +
                "          \"imageUrl\": null,\n" +
                "          \"quantity\": 100\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 2,\n" +
                "        \"name\": \"cpz2\",\n" +
                "        \"couponSn\": null,\n" +
                "        \"couponActivitySimpleRetVo\": {\n" +
                "          \"id\": 2,\n" +
                "          \"name\": \"cpz2\",\n" +
                "          \"beginTime\": \"2020-11-01T03:31:24\",\n" +
                "          \"endTime\": \"2021-01-31T03:31:38\",\n" +
                "          \"couponTime\": \"2020-12-01T03:31:44\",\n" +
                "          \"imageUrl\": null,\n" +
                "          \"quantity\": 200\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3,\n" +
                "        \"name\": \"cpz3\",\n" +
                "        \"couponSn\": null,\n" +
                "        \"couponActivitySimpleRetVo\": {\n" +
                "          \"id\": 2,\n" +
                "          \"name\": \"cpz2\",\n" +
                "          \"beginTime\": \"2020-11-01T03:31:24\",\n" +
                "          \"endTime\": \"2021-01-31T03:31:38\",\n" +
                "          \"couponTime\": \"2020-12-01T03:31:44\",\n" +
                "          \"imageUrl\": null,\n" +
                "          \"quantity\": 200\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"pageNum\": 1,\n" +
                "    \"pageSize\": 3,\n" +
                "    \"size\": 3,\n" +
                "    \"startRow\": 0,\n" +
                "    \"endRow\": 2,\n" +
                "    \"pages\": 1,\n" +
                "    \"prePage\": 0,\n" +
                "    \"nextPage\": 0,\n" +
                "    \"isFirstPage\": true,\n" +
                "    \"isLastPage\": true,\n" +
                "    \"hasPreviousPage\": false,\n" +
                "    \"hasNextPage\": false,\n" +
                "    \"navigatePages\": 8,\n" +
                "    \"navigatepageNums\": [\n" +
                "      1\n" +
                "    ],\n" +
                "    \"navigateFirstPage\": 1,\n" +
                "    \"navigateLastPage\": 1\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    //200: 第二页
    @Test
    public void getCouponList2() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        byte[] ret = manageClient.get()
                .uri("/coupons?page=5&pageSize=3&state=4")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 2,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 10,\n" +
                "        \"name\": \"cpz10\",\n" +
                "        \"couponSn\": null,\n" +
                "        \"couponActivitySimpleRetVo\": {\n" +
                "          \"id\": 2,\n" +
                "          \"name\": \"cpz2\",\n" +
                "          \"beginTime\": \"2020-11-01T03:31:24\",\n" +
                "          \"endTime\": \"2021-01-31T03:31:38\",\n" +
                "          \"couponTime\": \"2020-12-01T03:31:44\",\n" +
                "          \"imageUrl\": null,\n" +
                "          \"quantity\": 200\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 11,\n" +
                "        \"name\": \"cpz11\",\n" +
                "        \"couponSn\": null,\n" +
                "        \"couponActivitySimpleRetVo\": {\n" +
                "          \"id\": 2,\n" +
                "          \"name\": \"cpz2\",\n" +
                "          \"beginTime\": \"2020-11-01T03:31:24\",\n" +
                "          \"endTime\": \"2021-01-31T03:31:38\",\n" +
                "          \"couponTime\": \"2020-12-01T03:31:44\",\n" +
                "          \"imageUrl\": null,\n" +
                "          \"quantity\": 200\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"pageNum\": 4,\n" +
                "    \"pageSize\": 2,\n" +
                "    \"size\": 2,\n" +
                "    \"startRow\": 0,\n" +
                "    \"endRow\": 1,\n" +
                "    \"pages\": 1,\n" +
                "    \"prePage\": 0,\n" +
                "    \"nextPage\": 0,\n" +
                "    \"isFirstPage\": true,\n" +
                "    \"isLastPage\": true,\n" +
                "    \"hasPreviousPage\": false,\n" +
                "    \"hasNextPage\": false,\n" +
                "    \"navigatePages\": 8,\n" +
                "    \"navigatepageNums\": [\n" +
                "      1\n" +
                "    ],\n" +
                "    \"navigateFirstPage\": 1,\n" +
                "    \"navigateLastPage\": 1\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }


//    /**
//     * 使用优惠券
//     *
//     * @author 24320182203173 Chen Pinzhen
//     * @date: 2020/12/16 4:12
//     */
//
//    //200: 成功
//    @Test
//    public void useCoupon1() throws Exception {
//        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
//        byte[] ret = manageClient.put()
//                .uri("/coupons/20001")
//                .header("authorization", token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
//                .returnResult()
//                .getResponseBodyContent();
//        String responseString = new String(ret, "UTF-8");
//        String expectedResponse = "{\n" +
//                "  \"errno\": 0,\n" +
//                "  \"errmsg\": \"成功\"\n" +
//                "}";
//        JSONAssert.assertEquals(expectedResponse, responseString, false);
//    }
//
//    //905: 优惠券状态禁止
//    @Test
//    public void useCoupon2() throws Exception {
//        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
//        byte[] ret = manageClient.put()
//                .uri("/coupons/20012")
//                .header("authorization", token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.COUPON_STATENOTALLOW.getCode())
//                .returnResult()
//                .getResponseBodyContent();
//        String responseString = new String(ret, "UTF-8");
//        String expectedResponse = "{\n" +
//                "  \"errno\": 905,\n" +
//                "  \"errmsg\": \"优惠卷状态禁止\"\n" +
//                "}";
//        JSONAssert.assertEquals(expectedResponse, responseString, false);
//    }
//
//    /**
//         * 优惠券退回
//         * @author 24320182203173 Chen Pinzhen
//         * @date: 2020/12/16 5:28
//         */
//
//    //200: 成功
//    @Test
//    public void returnCoupon1() throws Exception {
//        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
//        byte[] ret = manageClient.put()
//                .uri("goods/shops/0/coupons/20012")
//                .header("authorization", token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
//                .returnResult()
//                .getResponseBodyContent();
//        String responseString = new String(ret, "UTF-8");
//        String expectedResponse = "{\n" +
//                "  \"errno\": 0,\n" +
//                "  \"errmsg\": \"成功\"\n" +
//                "}";
//        JSONAssert.assertEquals(expectedResponse, responseString, false);
//    }
//
//    //905: 优惠券状态禁止
//    @Test
//    public void returnCoupon2() throws Exception {
//        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
//        byte[] ret = manageClient.put()
//                .uri("goods/shops/0/coupons/20001")
//                .header("authorization", token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.COUPON_STATENOTALLOW.getCode())
//                .returnResult()
//                .getResponseBodyContent();
//        String responseString = new String(ret, "UTF-8");
//        String expectedResponse = "{\n" +
//                "  \"errno\": 905,\n" +
//                "  \"errmsg\": \"优惠卷状态禁止\"\n" +
//                "}";
//        JSONAssert.assertEquals(expectedResponse, responseString, false);
//    }



    /**
     * 领取优惠券
     * @author 24320182203173 Chen Pinzhen
     * @date: 2020/12/16 5:43
     */

    //200: 成功
    @Test
    public void receiveCoupon1() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        byte[] ret = manageClient.post()
                .uri("/couponactivities/20003/usercoupons")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 0\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    //905: 优惠券状态禁止--不允许领自己店内的优惠券
    @Test
    public void receiveCoupon2() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        byte[] ret = manageClient.post()
                .uri("/couponactivities/20001/usercoupons")
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.COUPON_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 0\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    //909: 未到领取优惠券时间
    @Test
    public void receiveCoupon3() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        byte[] ret = manageClient.post()
                .uri("/couponactivities/4/usercoupons")
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.COUPON_NOTBEGIN.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 909\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    //910: 优惠券领罄
    @Test
    public void receiveCoupon4() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        byte[] ret = manageClient.post()
                .uri("/couponactivities/20005/usercoupons")
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.COUPON_FINISH.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 910\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    //911: 优惠卷活动终止
    @Test
    public void receiveCoupon5() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDAzOTQ2OFEzIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMTMxODYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzA5NTg2fQ.F2cS4Qn16pDCxo5pL3GwqywN9neSdd-rLjXjM7306r4";
                //this.adminLogin("13088admin", "123456");
        byte[] ret = manageClient.post()
                .uri("/couponactivities/20006/usercoupons")
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.COUPON_END.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 911\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }


}

