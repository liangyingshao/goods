package cn.edu.xmu.goods.controller;
import cn.edu.xmu.goods.LoginVo;
import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * description: ZhangYueTest1
 * date: 2020/12/12 22:54
 * author: 张悦 10120182203143
 * version: 1.0
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class YueHaoTest {


//    @Value("${public-test.managementgate}")
//    private String managementGate;
//
//    @Value("${public-test.mallgate}")
//    private String mallGate;
//
//    private WebTestClient manageClient;
//
//    private WebTestClient mallClient;

    private String managementGate = "localhost:8090/goods";
    private String mallGate = "localhost:8090/goods";

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
    /** 1
     * 不需要登录-查询商品分类关系1-存在该分类
     **/
    @Test
    @Order(1)
    public void getCategorySubs() throws Exception {
        byte[] responseString=mallClient.get().uri("/categories/122/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"id\":123,\"pid\":122,\"name\":\"大师原作\",\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":124,\"pid\":122,\"name\":\"艺术衍生品\",\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 2
     * 不需要登录-查询商品分类关系2-不存在该分类
     **/
    @Test
    @Order(2)
    public void getCategorySubs2() throws Exception {//检测如果没有此id
        byte[]responseString=mallClient.get().uri("/categories/1000/subcategories")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /** 3
     * 不需要登录-查询商品分类关系3-该分类下无子分类
     **/
    @Test
    @Order(3)
    public void getCategorySubs3() throws Exception {
        byte[]responseString=mallClient.get().uri("/categories/123/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 4
     * 无需登录
     * 查询sku状态
     **/
    @Test
    @Order(4)
    public void getSkuStates() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus/states")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"未上架\",\"code\":0},{\"name\":\"上架\",\"code\":4},{\"name\":\"已删除\",\"code\":6}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 5
     * 无需登录
     * 查询所有sku-不加任何条件
     **/
    @Test
    @Order(5)
    public void getSkus1() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus?page=1&pageSize=2")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.data.total").isNumber()
                .jsonPath("$.data.pages").isNumber()
                .returnResult().getResponseBodyContent();

        String expectedResponse ="{\"errno\":0,\"data\":{\"pageSize\":2,\"page\":1,\"list\":[{\"id\":273,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disable\":false,\"price\":980000},{\"id\":274,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":850,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861cd259e57a.jpg\",\"inventory\":99,\"disable\":false,\"price\":850}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /** 6
     * 无需登录
     * 查询所有sku-spuId存在
     **/
    @Test
    @Order(6)
    public void getSkus2() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus?spuId=273&page=1&pageSize=2")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.data.total").isNumber()
                .jsonPath("$.data.pages").isNumber()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"pageSize\":2,\"page\":1,\"list\":[{\"id\":273,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disable\":false,\"price\":980000}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /** 7
     * 无需登录
     * 查询所有sku-shopId不存在
     **/
    @Test
    @Order(7)
    public void getSkus5() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus?shopId=100&page=1&pageSize=2")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.data.total").isNumber()
                .jsonPath("$.data.pages").isNumber()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"pageSize\":2,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /** 8
     * 无需登录
     * 查询所有sku-pageSize=1
     **/
    @Test
    @Order(8)
    public void getSkus6() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus?pageSize=1&page=1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.data.total").isNumber()
                .jsonPath("$.data.pages").isNumber()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"pageSize\":1,\"page\":1,\"list\":[{\"id\":273,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disable\":false,\"price\":980000}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /** 9
     * 无需登录
     * 查询所有sku-pageSize=5
     **/
    @Test
    @Order(9)
    public void getSkus7() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus?pageSize=5")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.data.total").isNumber()
                .jsonPath("$.data.pages").isNumber()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"pageSize\":5,\"page\":1,\"list\":[{\"id\":273,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disable\":false,\"price\":980000},{\"id\":274,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":850,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861cd259e57a.jpg\",\"inventory\":99,\"disable\":false,\"price\":850},{\"id\":275,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":4028,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861d65fa056a.jpg\",\"inventory\":10,\"disable\":false,\"price\":4028},{\"id\":276,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":6225,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861da5e7ec6a.jpg\",\"inventory\":10,\"disable\":false,\"price\":6225},{\"id\":277,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":16200,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861c5848ffc4.jpg\",\"inventory\":10,\"disable\":false,\"price\":16200}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }
    /** 10
     * 无需登录
     * 查询所有sku-page=2
     **/
    @Test
    @Order(10)
    public void getSkus8() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus?page=2")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.data.total").isNumber()
                .jsonPath("$.data.pages").isNumber()
                .returnResult().getResponseBodyContent();
        String expectedResponse =  "{\"errno\":0,\"data\":{\"pageSize\":10,\"page\":2,\"list\":[{\"id\":283,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":780000,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57faed5b5da32.jpg\",\"inventory\":1,\"disable\":false,\"price\":780000},{\"id\":284,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":880000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58620c598a78f.jpg\",\"inventory\":1,\"disable\":false,\"price\":880000},{\"id\":285,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1880000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58620cca773f3.jpg\",\"inventory\":1,\"disable\":false,\"price\":1880000},{\"id\":286,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1950000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58620dd2a854e.jpg\",\"inventory\":1,\"disable\":false,\"price\":1950000},{\"id\":287,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":2600000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58621005f229a.jpg\",\"inventory\":1,\"disable\":false,\"price\":2600000},{\"id\":288,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":550000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586211ad843f6.jpg\",\"inventory\":1,\"disable\":false,\"price\":550000},{\"id\":289,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":480000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586213438566e.jpg\",\"inventory\":1,\"disable\":false,\"price\":480000},{\"id\":290,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":180000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586214e020ab2.jpg\",\"inventory\":1,\"disable\":false,\"price\":180000},{\"id\":291,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":130000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586227f3cd5c9.jpg\",\"inventory\":1,\"disable\":false,\"price\":130000},{\"id\":292,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":200000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58621bd1768b4.jpg\",\"inventory\":1,\"disable\":false,\"price\":200000}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /** 11
     * 无需登录
     * 查询所有sku-page=9
     **/
    @Test
    @Order(11)
    public void getSkus9() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus?pageSize=9")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.data.total").isNumber()
                .jsonPath("$.data.pages").isNumber()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"pageSize\":9,\"page\":1,\"list\":[{\"id\":273,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disable\":false,\"price\":980000},{\"id\":274,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":850,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861cd259e57a.jpg\",\"inventory\":99,\"disable\":false,\"price\":850},{\"id\":275,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":4028,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861d65fa056a.jpg\",\"inventory\":10,\"disable\":false,\"price\":4028},{\"id\":276,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":6225,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861da5e7ec6a.jpg\",\"inventory\":10,\"disable\":false,\"price\":6225},{\"id\":277,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":16200,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861c5848ffc4.jpg\",\"inventory\":10,\"disable\":false,\"price\":16200},{\"id\":278,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1199,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cfb485e1df.jpg\",\"inventory\":46100,\"disable\":false,\"price\":1199},{\"id\":279,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1199,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cfc4323959.jpg\",\"inventory\":500,\"disable\":false,\"price\":1199},{\"id\":280,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":2399,\"imageUrl\":\"http://47.52.88.176/file/images/201611/file_583af4aec812c.jpg\",\"inventory\":1834,\"disable\":false,\"price\":2399},{\"id\":281,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1380000,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57fae8f7240c6.jpg\",\"inventory\":1,\"disable\":false,\"price\":1380000}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /** 12
     * 无需登录
     * 查询评论的所有状态
     **/
    @Test
    @Order(12)
    public void getCommentStates() throws Exception {
        byte[]responseString=mallClient.get().uri("/comments/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"未审核\",\"code\":0},{\"name\":\"评论成功\",\"code\":1},{\"name\":\"未通过\",\"code\":2}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /** 13
     * 无需登录
     * 查询优惠券的所有状态
     **/
    @Test
    @Order(13)
    public void getCouponStates() throws Exception {
        byte[]responseString=mallClient.get().uri("/coupons/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"未领取\",\"code\":0},{\"name\":\"已领取\",\"code\":1},{\"name\":\"已使用\",\"code\":2},{\"name\":\"已失效\",\"code\":3}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /** 14
     * 无需登录
     * 查询预售活动的所有状态
     **/
    @Test
    @Order(14)
    public void getPreSaleStates() throws Exception {
        byte[]responseString=mallClient.get().uri("/presales/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"已下线\",\"code\":0},{\"name\":\"已上线\",\"code\":1},{\"name\":\"已删除\",\"code\":2}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /** 15
     * 无需登录
     * 查询团购活动的所有状态
     **/
    @Test
    @Order(15)
    public void getGrouponStates() throws Exception {
        byte[]responseString=mallClient.get().uri("/groupons/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"已下线\",\"code\":0},{\"name\":\"已上线\",\"code\":1},{\"name\":\"已删除\",\"code\":2}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /** 16
     * 无需登录
     * 查询店铺的所有状态
     **/
    @Test
    @Order(16)
    public void getShopStates() throws Exception {
        byte[]responseString=mallClient.get().uri("/shops/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"name\": \"未审核\",\n" +
                "      \"code\": 0\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"未上线\",\n" +
                "      \"code\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"上线\",\n" +
                "      \"code\": 2\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"关闭\",\n" +
                "      \"code\": 3\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"审核未通过\",\n" +
                "      \"code\": 4\n" +
                "    }\n" +
                "  ],\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
        System.out.println(new String(responseString, "UTF-8"));
        // JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /** 17
     * 需管理员登录
     * 新增类目
     **/
    @Test
    @Order(17)
    public void insertCategoryTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        String roleJson = "{\"name\": \"test\"}";
        byte[] responseString = manageClient.post().uri("/shops/0/categories/139/subcategories")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);

        byte[] responseString2=mallClient.get().uri("/categories/139/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":0,\"data\":[{\"id\":20142,\"pid\":139,\"name\":\"test\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /** 18
     * 需管理员登录
     * 修改分类
     **/
    @Test
    @Order(18)
    public void modifyCategoryTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        String roleJson = "{\"name\": \"testCategory\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/categories/126")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);

        byte[] responseString2=mallClient.get().uri("/categories/125/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":0,\"data\":[{\"id\":126,\"pid\":125,\"name\":\"testCategory\"},{\"id\":133,\"pid\":125,\"name\":\"腕表\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /** 19
     * 需管理员登录
     * 删除分类-删除子分类
     **/
    @Test
    @Order(19)
    public void deleteCategoryTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/categories/123")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);

        byte[] responseString2=mallClient.get().uri("/categories/122/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":0,\"data\":[{\"id\":124,\"pid\":122,\"name\":\"艺术衍生品\",\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /** 20
     * 需管理员登录
     * 删除分类-删除分类
     **/
    @Test
    @Order(20)
    public void deleteCategoryTest2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/categories/131")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);

        byte[] responseString2=mallClient.get().uri("/categories/131/subcategories")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /** 21
     * 无需登录
     * 获取品牌
     **/
    @Test
    @Order(21)
    public void getBrands() throws Exception {
        byte[]responseString=mallClient.get().uri("/brands")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":53,\"pages\":6,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":71,\"name\":\"戴荣华\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":72,\"name\":\"范敏祺\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":73,\"name\":\"黄卖九\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":74,\"name\":\"李进\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":75,\"name\":\"李菊生\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":76,\"name\":\"李小聪\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":77,\"name\":\"刘伟\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":78,\"name\":\"陆如\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":79,\"name\":\"秦锡麟\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":80,\"name\":\"舒慧娟\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 22
     * 无需登录
     * 获取品牌-page=5
     **/
    @Test
    @Order(22)
    public void getBrands2() throws Exception {
        byte[]responseString=mallClient.get().uri("/brands?page=5")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":53,\"pages\":6,\"pageSize\":10,\"page\":5,\"list\":[{\"id\":111,\"name\":\"孙星池\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":112,\"name\":\"林祺龙\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":113,\"name\":\"故宫\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":114,\"name\":\"方毅\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":115,\"name\":\"李晓辉\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":116,\"name\":\"揭金平\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":117,\"name\":\"杨曙华\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":118,\"name\":\"方冬生\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":119,\"name\":\"皇家窑火\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":20120,\"name\":\"cpz1\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 23
     * 管理员登录
     * 修改品牌-品牌ID不存在
     **/
    @Test
    @Order(23)
    public void modifyBrandTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        String roleJson = "{\"name\": \"testBrand\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/brands/126")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }
    /** 24
     * 需管理员登录
     * 修改品牌信息
     **/
    @Test
    @Order(24)
    public void modifyBrandTest2() throws Exception {
        String token = this.login("13088admin", "123456");
        String roleJson = "{\"name\": \"testBrand\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/brands/71")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);
        byte[]responseString2=mallClient.get().uri("/brands?pageSize=2")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":0,\"data\":{\"total\":53,\"pages\":27,\"pageSize\":2,\"page\":1,\"list\":[{\"id\":71,\"name\":\"testBrand\",\"detail\":null,\"imageUrl\":null},{\"id\":72,\"name\":\"范敏祺\",\"detail\":null,\"imageUrl\":null}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }

    /** 25
     * 需管理员登录
     * 修改品牌信息
     **/
    @Test
    @Order(25)
    public void modifyBrandTest3() throws Exception {
        String token = this.login("13088admin", "123456");
        String roleJson = "{\"name\": \"testBrand\",\"detail\": \"testBrandDetail\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/brands/71")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);
        byte[]responseString2=mallClient.get().uri("/brands?pageSize=2")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":0,\"data\":{\"total\":53,\"pages\":27,\"pageSize\":2,\"page\":1,\"list\":[{\"id\":71,\"name\":\"testBrand\",\"detail\":\"testBrandDetail\",\"imageUrl\":null},{\"id\":72,\"name\":\"范敏祺\",\"detail\":null,\"imageUrl\":null}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /** 26
     * 需管理员登录
     * 删除品牌信息-品牌id不存在
     **/
    @Test
    public void deleteBrandTest() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/brands/10000")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }
    /** 27
     * 需管理员登录
     * 删除商品分类-分类id存在
     **/
    @Test
    @Order(27)
    public void deleteCategoryTest3() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/categories/10000")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }
    /** 28
     * 需管理员登录
     * 删除品牌
     **/
    @Test
    @Order(28)
    public void deleteBrandTest2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/brands/71")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);
        byte[] responseString2=mallClient.get().uri("/brands?pageSize=2")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":0,\"data\":{\"total\":52,\"pages\":26,\"pageSize\":2,\"page\":1,\"list\":[{\"id\":72,\"name\":\"范敏祺\",\"detail\":null,\"imageUrl\":null},{\"id\":73,\"name\":\"黄卖九\",\"detail\":null,\"imageUrl\":null}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /** 29
     * 需管理员登录
     * 修改分类-分类id不存在
     **/
    @Test
    @Order(29)
    public void modifyCategoryTest() throws Exception {
        String token = this.login("13088admin", "123456");
        String roleJson = "{\"name\": \"testCategory\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/categories/10000")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse ="{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }
    /** 30
     * 需管理员登录
     * 删除商品sku-skuid存在
     **/
    @Test
    @Order(30)
    public void deleteSkuTest() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/skus/10000")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }




    private String login(String userName, String password) throws Exception {
//        LoginVo vo = new LoginVo();
//        vo.setUserName(userName);
//        vo.setPassword(password);
//        String requireJson = JacksonUtil.toJson(vo);
//        byte[] ret = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
//                .returnResult()
//                .getResponseBodyContent();
//        return JacksonUtil.parseString(new String(ret, "UTF-8"), "data");
        return creatTestToken(1L, 0L, 100);
        //endregion
    }

    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        return token;
    }

}
