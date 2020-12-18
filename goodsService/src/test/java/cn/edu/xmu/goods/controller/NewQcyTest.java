package cn.edu.xmu.goods.controller;
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
public class NewQcyTest {
//    @Value("${public-test.managementgate}")
//    private String managementGate;
//    @Value("${public-test.mallgate}")
//    private String mallGate;
    private String managementGate = "127.0.0.1:8881";
    private String mallGate = "127.0.0.1:8880";

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
    /**
     * 获取SKU所有状态 成功
     * @throws Exception
     */
    @Test
    public void getSkuAllStates() throws Exception {
        byte[] ret = mallClient.get()
                .uri("/skus/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"未上架\",\"code\":0},{\"name\":\"上架\",\"code\":4},{\"name\":\"已删除\",\"code\":6}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 获取商店所有状态 成功
     */
    @Test
    public void getshopAllStates() throws Exception {
        byte[] ret = mallClient.get()
                .uri("/shops/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"未审核\",\"code\":0},{\"name\":\"未上线\",\"code\":1},{\"name\":\"上线\",\"code\":2},{\"name\":\"关闭\",\"code\":3},{\"name\":\"审核未通过\",\"code\":4}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }


    /**
     * 店家新建商品SPU 成功
     */
    @Test
    public void addSpuTest1() throws Exception {
//        String token = this.adminLogin("13088admin", "123456");
        //String token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE3MDAyNjM2NTFRIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgxMzk1OTYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MTM1OTk2fQ.yBb0uUJTf8zmQIK0BYWXVARxGX49D5Lrv4-y1GB8fzE";
        JSONObject body = new JSONObject();
        body.put("name", "iphone13");
        body.put("description", "最新系列");
        body.put("specs", "{\"id\":1,\"name\":\"ipadspec\", \"specItems\":{\"id\":1, \"name\":\"color\"},{\"id\":2, \"name\":\"memory\"}}\" }");
        String skuJson = body.toJSONString();

        byte[] responseString = manageClient.post().uri("/shops/2/spus")
                .header("authorization",token)
                .bodyValue(skuJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data.name").isEqualTo("iphone13")
                .jsonPath("$.data.detail").isEqualTo("最新系列")
                .jsonPath("$.data.disable").isEqualTo("false")
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家新建商品SPU 商铺已关闭
     */
    @Test
    public void addSpuTest2() throws Exception {
//        String token = this.adminLogin("13088admin", "123456");
        JSONObject body = new JSONObject();
        body.put("name", "milk");
        body.put("description", "高蛋白");
        body.put("specs", "{\"id\":1,\"name\":\"ipadspec\", \"specItems\":{\"id\":1, \"name\":\"flavor\"},{\"id\":2, \"name\":\"unit\"}}\" }");
        String skuJson = body.toJSONString();
        byte[] responseString=null;
        responseString = manageClient.post().uri("/shops/10/spus")
                .header("authorization",token)
                .bodyValue(skuJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.SHOP_NOTOPERABLE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }
    /**
     * 店家新建商品SPU 商铺未通过审核
     */
    @Test
    public void addSpuTest3() throws Exception {
//        String token = this.adminLogin("13088admin", "123456");
        JSONObject body = new JSONObject();
        body.put("name", "milk");
        body.put("description", "高蛋白");
        body.put("specs", "{\"id\":1,\"name\":\"ipadspec\", \"specItems\":{\"id\":1, \"name\":\"flavor\"},{\"id\":2, \"name\":\"unit\"}}\" }");
        String skuJson = body.toJSONString();
        byte[] responseString=null;


        responseString = manageClient.post().uri("/shops/6/spus")
                .header("authorization",token)
                .bodyValue(skuJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.SHOP_NOTOPERABLE.getCode())
                .returnResult()
                .getResponseBodyContent();


    }
    /**
     * 店家新建商品SPU 商铺未审核
     */
    @Test
    public void addSpuTest4() throws Exception {
//        String token = this.adminLogin("13088admin", "123456");
        JSONObject body = new JSONObject();
        body.put("name", "milk");
        body.put("description", "高蛋白");
        body.put("specs", "{\"id\":1,\"name\":\"ipadspec\", \"specItems\":{\"id\":1, \"name\":\"flavor\"},{\"id\":2, \"name\":\"unit\"}}\" }");
        String skuJson = body.toJSONString();
        byte[] responseString=null;

        responseString = manageClient.post().uri("/shops/1/spus")
                .header("authorization",token)
                .bodyValue(skuJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.SHOP_NOTOPERABLE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }
    /**
     * description: 查看一条商品SPU的详细信息 (成功)
     */
    @Test
    public void showSpuTest1() throws Exception {
        byte[] responseString = mallClient.get().uri("/spus/10005")
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(10005)
                .jsonPath("$.data.name").isEqualTo("spu10005")
                .jsonPath("$.data.freight.name").isEqualTo("测试模板3")
                .jsonPath("$.data.freight.id").isEqualTo(11)
                .jsonPath("$.data.category.name").isEqualTo("邮品")
                .jsonPath("$.data.brand.name").isEqualTo("中国集邮总公司")
                .returnResult()
                .getResponseBodyContent();

    }
    /**
     * description: 查看一条商品SPU的详细信息 (成功)
     */
    @Test
    public void showSpuTest2() throws Exception {


        byte[] responseString = mallClient.get().uri("/spus/9001")
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(9001)
                .jsonPath("$.data.name").isEqualTo("ysl")
                .jsonPath("$.data.freight.name").isEqualTo("测试模板3")
                .jsonPath("$.data.freight.id").isEqualTo(11)
                .jsonPath("$.data.category.name").isEqualTo("大师原作")
                .jsonPath("$.data.brand.name").isEqualTo("戴荣华")
                .returnResult()
                .getResponseBodyContent();
    }
    /**
     * description: 查看一条商品SPU的详细信息 该SPU的disable==true,不可查看
     */
    @Test
    public void showSpuTest3() throws Exception {
        byte[] responseString = mallClient.get().uri("/spus/10004")
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.SPU_NOTOPERABLE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }
    /**
     * description: 查看一条商品SPU的详细信息 spuId不存在
     * date: 2020/12/03 19：33
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void showSpuTest4() throws Exception {

        byte[] responseString = mallClient.get().uri("/spus/100001")
                .header("authorization",token)
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

    }


    /**
     * description: 店家修改SPU 成功
     */
    @Test
    public void modifySpuTest1() throws Exception {
        //String token = this.adminLogin("13088admin", "123456");
        JSONObject body = new JSONObject();
        body.put("name", "milk");
        body.put("description", "高蛋白");
        body.put("specs", "{\"id\":1,\"name\":\"ipadspec\", \"specItems\":{\"id\":1, \"name\":\"flavor\"},{\"id\":2, \"name\":\"unit\"}}\" }");
        String skuJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("shops/1/spus/10005").header("authorization", token)
                .bodyValue(skuJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

        byte[] responseString1 = mallClient.get().uri("/spus/10005")
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(10005)
                .jsonPath("$.data.name").isEqualTo("milk")
                .jsonPath("$.data.detail").isEqualTo("高蛋白")
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 店家修改SPU 修改的spu与shopID不对应
     */
    @Test
    public void modifySpuTest2() throws Exception {


        String skuJson = "{\"name\":\"iphone13\",\"decription\":\"最新系列\",\"specs\": \"{\"id\":1,\"name\":\"ipadspec\", \"specItems\":{\"id\":1, \"name\":\"size\"},{\"id\":2, \"name\":\"color\"},{\"id\":3, \"name\":\"memory\"}}\" }";
        byte[] responseString = manageClient.put().uri("shops/2/spus/8000").header("authorization", token)
                .bodyValue(skuJson).exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * description: 店家修改SPU spu不存在
     */
    @Test
    public void modifySpuTest3() throws Exception {
        //String token = this.adminLogin("13088admin", "123456");
        //String token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE3MDAyNjM2NTFRIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgxMzk1OTYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MTM1OTk2fQ.yBb0uUJTf8zmQIK0BYWXVARxGX49D5Lrv4-y1GB8fzE";
        String skuJson = "{\"name\":\"iphone13\",\"decription\":\"最新系列\",\"specs\": \"{\"id\":1,\"name\":\"ipadspec\", \"specItems\":{\"id\":1, \"name\":\"size\"},{\"id\":2, \"name\":\"color\"},{\"id\":3, \"name\":\"memory\"}}\" }";
        byte[] responseString = manageClient.put().uri("shops/2/spus/898989").header("authorization", token)
                .bodyValue(skuJson).exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * description: 将SPU加入二级分类 (成功)
     */
    @Test
    public void addSpuCategoryTest1() throws Exception {
        //String token = this.adminLogin("13088admin", "123456");
        //String token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE3MDAyNjM2NTFRIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgxMzk1OTYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MTM1OTk2fQ.yBb0uUJTf8zmQIK0BYWXVARxGX49D5Lrv4-y1GB8fzE";

        byte[] responseString = manageClient.put().uri("/shops/2/spus/8000/categories/124").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 将SPU加入二级分类 (试图加入一级分类)
     */
    @Test
    public void addSpuCategoryTest2() throws Exception {
        //String token = this.adminLogin("13088admin", "123456");
        //String token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE3MDAyNjM2NTFRIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgxMzk1OTYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MTM1OTk2fQ.yBb0uUJTf8zmQIK0BYWXVARxGX49D5Lrv4-y1GB8fzE";

        byte[] responseString = manageClient.put().uri("/shops/2/spus/8000/categories/122").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.CATEALTER_INVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.CATEALTER_INVALID.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 将SPU加入二级分类 (试图加入不存在分类)
     */
    @Test
    public void addSpuCategoryTest3() throws Exception {
        //String token = this.adminLogin("13088admin", "123456");
        //String token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE3MDAyNjM2NTFRIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgxMzk1OTYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MTM1OTk2fQ.yBb0uUJTf8zmQIK0BYWXVARxGX49D5Lrv4-y1GB8fzE";
        String skuJson = "{\"name\":\"iphone13\",\"decription\":\"最新系列\",\"specs\": \"{\"id\":1,\"name\":\"ipadspec\", \"specItems\":{\"id\":1, \"name\":\"size\"},{\"id\":2, \"name\":\"color\"},{\"id\":3, \"name\":\"memory\"}}\" }";
        byte[] responseString = manageClient.put().uri("/shops/2/spus/8000/categories/666").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * description: 移除SPU分类 (成功)
     */
    @Test
    public void removeSpuCategoryTest1() throws Exception {
        //String token = this.adminLogin("13088admin", "123456");
        //String token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE3MDAyNjM2NTFRIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgxMzk1OTYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MTM1OTk2fQ.yBb0uUJTf8zmQIK0BYWXVARxGX49D5Lrv4-y1GB8fzE";
        byte[] responseString = manageClient.delete().uri("/shops/2/spus/8001/categories/124").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * description: 移除SPU分类 分类不存在
     */
    @Test
    public void removeSpuCategoryTest2() throws Exception {
        //String token = this.adminLogin("13088admin", "123456");
        //String token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE3MDAyNjM2NTFRIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgxMzk1OTYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MTM1OTk2fQ.yBb0uUJTf8zmQIK0BYWXVARxGX49D5Lrv4-y1GB8fzE";
        byte[] responseString = manageClient.delete().uri("/shops/2/spus/8000/categories/888").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * description: 移除SPU分类 移出分类与SPU原分类不一致
     */
    @Test
    public void removeSpuCategoryTest3() throws Exception {
        //String token = this.adminLogin("13088admin", "123456");
        //String token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE3MDAyNjM2NTFRIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgxMzk1OTYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MTM1OTk2fQ.yBb0uUJTf8zmQIK0BYWXVARxGX49D5Lrv4-y1GB8fzE";
        byte[] responseString = manageClient.delete().uri("/shops/2/spus/8001/categories/126").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.CATEALTER_INVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 将SPU加入品牌 (成功)
     */
    @Test
    public void addSpuBrandTest1() throws Exception {

        //String token = this.adminLogin("13088admin", "123456");
        //String token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE3MDAyNjM2NTFRIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgxMzk1OTYsInVzZXJJZCI6MSwiaWF0IjoxNjA4MTM1OTk2fQ.yBb0uUJTf8zmQIK0BYWXVARxGX49D5Lrv4-y1GB8fzE";
        byte[] responseString = manageClient.put().uri("/shops/2/spus/8004/brands/71").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 将SPU加入品牌 试图加入不存在的品牌
     */
    @Test
    public void addSpuBrandTest2() throws Exception {
        //String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/2/spus/8001/brands/8888").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 移除SPU品牌 (成功)
     */
    @Test
    public void removeSpuBrandTest1() throws Exception {
        //String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/2/spus/8000/brands/71").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 移除SPU品牌 (移出品牌与SPU原品牌不一致)
     * date: 2020/12/02 22：52
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void removeSpuBrandTest2() throws Exception {
        //String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.delete().uri("/shops/2/spus/8003/brands/91").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.BRANDALTER_INVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 逻辑删除商品SPU (成功)
     */
    @Test
    public void deleteSpuBrandTest1() throws Exception {
       //String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/2/spus/8003").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 逻辑删除商品SPU 不存在
     */
    @Test
    public void deleteSpuBrandTest2() throws Exception {
        //String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.delete().uri("/shops/2/spus/898989").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }
}


