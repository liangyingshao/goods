package cn.edu.xmu.goods.controller;
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
@SpringBootTest(classes = Application.class)   //标识本类是一个SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ZhangYueTest1 {


//    @Value("${public-test.managementgate}")
//    private String managementGate;
//
//    @Value("${public-test.mallgate}")
//    private String mallGate;
//
//    private WebTestClient manageClient;
//
//    private WebTestClient mallClient;

    private String managementGate = "localhost:8090";
    private String mallGate = "localhost:8090";

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

    /**
     * 获取所有品牌（第一页）
     * @throws Exception
     */
    @Test
    @Order(00)
    public void findAllBrands1() throws Exception{

        byte[] responseString = mallClient.get().uri("/goods/brands?page=1&pageSize=2")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
//                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 2,\n" +
                "    \"total\": 3,\n" +
                "    \"pages\": 2,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1171,\n" +
                "        \"name\": \"戴荣\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\": null,\n" +
                "        \"gmtCreate\": \"2020-11-28T17:42:21\",\n" +
                "        \"gmtModified\": \"2020-11-28T17:42:21\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 1172,\n" +
                "        \"name\": \"范敏\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\": null,\n" +
                "        \"gmtCreate\": \"2020-11-28T17:42:21\",\n" +
                "        \"gmtModified\": \"2020-11-28T17:42:21\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 获取所有品牌（第二页）
     * @throws Exception
     */
    @Test
    @Order(00)
    public  void findAllBrands2() throws Exception {
        byte[] responseString = mallClient.get().uri("/goods/brands?page=2&pageSize=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
//                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 2,\n" +
                "    \"pageSize\": 1,\n" +
                "    \"total\": 3,\n" +
                "    \"pages\": 3,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1172,\n" +
                "        \"name\": \"范敏\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\": null,\n" +
                "        \"gmtCreate\": \"2020-11-28T17:42:21\",\n" +
                "        \"gmtModified\": \"2020-11-28T17:42:21\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * description: 新增品牌 品牌名称已存在
     * version: 1.0
     * date: 2020/12/2 20:47
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    @Order(01)
    public void insertBrandTest2() {

        String token = creatTestToken(1L, 0L, 100);

        JSONObject body = new JSONObject();
        body.put("name", "黄卖");
        body.put("detail", null);
        String brandJson = body.toJSONString();

        byte[] responseString = manageClient.post().uri("/goods/shops/{shopId}/brands",0)
                .header("authorization", token)
                .bodyValue(brandJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.BRAND_NAME_SAME.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.BRAND_NAME_SAME.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 新增品牌 品牌名称为空
     * version: 1.0
     * date: 2020/12/2 20:48
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    @Order(01)
    public void insertBrandTest3() throws Exception{

        JSONObject body = new JSONObject();
        body.put("name", "");
        body.put("detail", "test");
        String brandJson = body.toJSONString();

        String token = creatTestToken(1L, 0L, 100);
        byte[] responseString = manageClient.post().uri("/goods/shops/{shopId}/brands",0)
                .header("authorization", token)
                .bodyValue(brandJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
 //               .jsonPath("$.errmsg").isEqualTo("品牌名称不能为空;")
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 未登录新增品牌
     * version: 1.0
     * date: 2020/12/2 20:48
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    @Order(01)
    public void insertBrandTest4() throws Exception {
        JSONObject body = new JSONObject();
        body.put("name", "test");
        body.put("detail", "test");
        String brandJson = body.toJSONString();

        byte[] responseString = manageClient.post().uri("/goods/shops/{shopId}/brands",0)
                .bodyValue(brandJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 伪造token新增品牌
     * version: 1.0
     * date: 2020/12/2 20:48
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    @Order(01)
    public void insertBrandTest5() throws Exception {
        JSONObject body = new JSONObject();
        body.put("name", "test");
        body.put("detail", "test");
        String brandJson = body.toJSONString();

        byte[] responseString = manageClient.post().uri("/goods/shops/{shopId}/brands",0)
                .header("authorization", "test")
                .bodyValue(brandJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 新增品牌 （成功）
     * version: 1.0
     * date: 2020/12/2 20:47
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    @Order(01)
    public void insertBrandTest1()throws Exception {
        JSONObject body = new JSONObject();
        body.put("name", "test");
        body.put("detail", "test");
        String brandJson = body.toJSONString();

        String token = creatTestToken(1L, 0L, 100);

        byte[] responseString = manageClient.post().uri("/goods/shops/{shopId}/brands",0)
                .header("authorization", token)
                .bodyValue(brandJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "        \"name\": \"test\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\": test\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse,  new String(responseString, StandardCharsets.UTF_8), false);

        //查询验证品牌新增成功
        byte[] responseString2 = mallClient.get().uri("/goods/brands?page=1&pageSize=4")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse2 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 4,\n" +
                "    \"total\": 4,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1171,\n" +
                "        \"name\": \"戴荣\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\": null,\n" +
                "        \"gmtCreate\": \"2020-11-28T17:42:21\",\n" +
                "        \"gmtModified\": \"2020-11-28T17:42:21\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 1172,\n" +
                "        \"name\": \"范敏\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\": null,\n" +
                "        \"gmtCreate\": \"2020-11-28T17:42:21\",\n" +
                "        \"gmtModified\": \"2020-11-28T17:42:21\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 1173,\n" +
                "        \"name\": \"黄卖\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\": null,\n" +
                "        \"gmtCreate\": \"2020-12-03T21:04:55\",\n" +
                "        \"gmtModified\": \"2020-12-03T21:04:55\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"test\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\": \"test\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, StandardCharsets.UTF_8), false);
    }

    /**
     * description: 修改品牌 (查无此品牌)
     * version: 1.0
     * date: 2020/12/2 20:48
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    @Order(02)
    public void modifyBrand2() throws Exception {
        JSONObject body = new JSONObject();
        body.put("name", "test");
        body.put("detail", "test");
        String brandJson = body.toJSONString();
        String token = creatTestToken(1L, 0L, 100);
        //String token = this.login("13088admin","123456");

        byte[] responseString = manageClient.put().uri("/goods/shops/{shopId}/brands/{id}", 0, 99)
                .header("authorization", token)
                .bodyValue(brandJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 修改品牌(品牌名称已存在)
     * version: 1.0
     * date: 2020/12/2 20:48
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    @Order(02)
    public void modifyBrand3() throws Exception {

        JSONObject body = new JSONObject();
        body.put("name", "黄卖");
        body.put("detail", "test");
        String brandJson = body.toJSONString();

        String token = creatTestToken(1L, 0L, 100);

        byte[] responseString = manageClient.put().uri("/goods/shops/{shopId}/brands/{id}", 0, 1171)
                .header("authorization", token)
                .bodyValue(brandJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.BRAND_NAME_SAME.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.BRAND_NAME_SAME.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * description: 未登录修改品牌
     * version: 1.0
     * date: 2020/12/2 20:48
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    @Order(02)
    public void modifyBrand4() throws Exception {
        JSONObject body = new JSONObject();
        body.put("name", "test");
        body.put("detail", "test");
        String brandJson = body.toJSONString();

        byte[] responseString = manageClient.put().uri("/goods/shops/{shopId}/brands/{id}", 0, 1171)
                .bodyValue(brandJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 伪造token修改品牌
     * version: 1.0
     * date: 2020/12/2 20:48
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    @Order(02)
    public void modifyBrand5() throws Exception {
        JSONObject body = new JSONObject();
        body.put("name", "test");
        body.put("detail", "test");
        String brandJson = body.toJSONString();

        byte[] responseString = manageClient.put().uri("/goods/shops/{shopId}/brands/{id}", 0, 1171)
                .header("authorization", "test")
                .bodyValue(brandJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * description: 修改品牌(成功)
     * version: 1.0
     * date: 2020/12/2 20:48
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    @Order(02)
    public void modifyBrand1() throws Exception {
        String token = this.creatTestToken(1L, 0L, 100);

        JSONObject body = new JSONObject();
        body.put("name", "test1");
        body.put("detail", "test");
        String brandJson = body.toJSONString();

        byte[] responseString = manageClient.put().uri("/goods/shops/{shopId}/brands/{id}", 0, 1172)
                .header("authorization", token)
                .bodyValue(brandJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        //查询验证品牌修改成功
        byte[] responseString2 = mallClient.get().uri("/goods/brands?page=1&pageSize=4")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse2 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 4,\n" +
                "    \"total\": 4,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1171,\n" +
                "        \"name\": \"戴荣\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\": null,\n" +
                "        \"gmtCreate\": \"2020-11-28T17:42:21\",\n" +
                "        \"gmtModified\": \"2020-11-28T17:42:21\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 1172,\n" +
                "        \"name\": \"test1\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\":\"test\",\n" +
                "        \"gmtCreate\": \"2020-11-28T17:42:21\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 1173,\n" +
                "        \"name\": \"黄卖\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\": null,\n" +
                "        \"gmtCreate\": \"2020-12-03T21:04:55\",\n" +
                "        \"gmtModified\": \"2020-12-03T21:04:55\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"test\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\": \"test\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, StandardCharsets.UTF_8), false);
    }


    /**
     * description: 删除品牌，品牌下的spu成为没有品牌的spu，品牌id=0
     * version: 1.0
     * date: 2020/12/2 20:48
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    @Order(03)
    public void deleteBrandTest5() throws Exception{
        String token = this.creatTestToken(1L, 0L, 100);

        byte[] responseString1 = manageClient.delete().uri("/goods/shops/{shopId}/brands/{id}", 0, 1173)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        //查询验证品牌删除成功
        byte[] responseString2 = mallClient.get().uri("/goods/brands?page=1&pageSize=4")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse2 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 4,\n" +
                "    \"total\": 3,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1171,\n" +
                "        \"name\": \"戴荣\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\": null,\n" +
                "        \"gmtCreate\": \"2020-11-28T17:42:21\",\n" +
                "        \"gmtModified\": \"2020-11-28T17:42:21\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 1172,\n" +
                "        \"name\": \"test1\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\":\"test\",\n" +
                "        \"gmtCreate\": \"2020-11-28T17:42:21\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"test\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\": \"test\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, StandardCharsets.UTF_8), false);

        //查询验证品牌下的spu是否变成没有品牌的spu
        byte[] responseString3 = mallClient.get().uri("/goods/spus/11274")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse3 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"id\": 11274,\n" +
                "    \"brand\": {\n" +
                "      \"id\": 0\n" +
                "    }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse3, new String(responseString3, StandardCharsets.UTF_8), false);
    }


    /**
     * description: 删除品牌(id不存在)
     * version: 1.0
     * date: 2020/12/2 20:49
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    @Order(03)
    public void deleteBrandTest2() {
        String token = this.creatTestToken(1L, 0L, 100);
        //String token = this.login("13088admin","123456");
        byte[] responseString = manageClient.delete().uri("/goods/shops/0/brands/1")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo("操作的资源id不存在")
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * description: 伪造token删除品牌
     * version: 1.0
     * date: 2020/12/2 20:48
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    @Order(03)
    public void deleteBrandTest3() {
        byte[] responseString = manageClient.delete().uri("/goods/shops/0/brands/1172")
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 未登录删除品牌
     * version: 1.0
     * date: 2020/12/2 20:48
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    @Order(03)
    public void deleteBrandTest4() {
        byte[] responseString = manageClient.delete().uri("/goods/shops/0/brands/1172", 0, 0)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }


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
    @Order(00)
    public void listSubcategories1() throws Exception{

        byte[] responseString = mallClient.get().uri("/goods/categories/11122/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse="{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": 11123,\n" +
                "      \"pid\": 11122,\n" +
                "      \"name\": \"大师原作\",\n" +
                "      \"gmtCreate\": \"2020-11-28T17:42:20\",\n" +
                "      \"gmtModified\": \"2020-11-28T17:42:20\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 11124,\n" +
                "      \"pid\": 11122,\n" +
                "      \"name\": \"艺术衍生品\",\n" +
                "      \"gmtCreate\": \"2020-11-28T17:42:20\",\n" +
                "      \"gmtModified\": \"2020-11-28T17:42:20\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);
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
    @Order(00)
    public void listSubcategories2() throws Exception{
        byte[] responseString = mallClient.get().uri("/goods/categories/11199/subcategories")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();
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
    @Order(01)
    public void insertGoodsCategoryTest1()throws Exception {

        JSONObject body = new JSONObject();
        body.put("name", "test2");
        String goodsCategoryJson = body.toJSONString();

        String token = creatTestToken(1L, 0L, 100);

        byte[] responseString = manageClient.post().uri("/goods/shops/0/categories/0/subcategories")
                .header("authorization", token)
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "        \"pid\":0,\n" +
                "        \"name\": \"test2\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse,  new String(responseString, StandardCharsets.UTF_8), false);

        //查询验证分类新增成功
        byte[] responseString2 = mallClient.get().uri("/goods/categories/0/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse2 ="{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": 11122,\n" +
                "      \"pid\": 0,\n" +
                "      \"name\": \"艺术品\",\n" +
                "      \"gmtCreate\": \"2020-11-28T17:42:20\",\n" +
                "      \"gmtModified\": \"2020-11-28T17:42:20\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 11125,\n" +
                "      \"pid\": 0,\n" +
                "      \"name\": \"收藏品\",\n" +
                "      \"gmtCreate\": \"2020-11-28T17:42:20\",\n" +
                "      \"gmtModified\": \"2020-11-28T17:42:20\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 11127,\n" +
                "      \"pid\": 0,\n" +
                "      \"name\": \"高端日用品\",\n" +
                "      \"gmtCreate\": \"2020-11-28T17:42:20\",\n" +
                "      \"gmtModified\": \"2020-11-28T17:42:20\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 11131,\n" +
                "      \"pid\": 0,\n" +
                "      \"name\": \"建行专享\",\n" +
                "      \"gmtCreate\": \"2020-11-28T17:42:20\",\n" +
                "      \"gmtModified\": \"2020-11-28T17:42:20\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"pid\": 0,\n" +
                "      \"name\": \"test2\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";;

        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, StandardCharsets.UTF_8), false);

    }

    /**
     * description: 新增商品二级类目,pid=131 （成功）
     * version: 1.0
     * date: 2020/12/2 20:47
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    @Order(01)
    public void insertSubGoodsCategoryTest1()throws Exception {

        JSONObject body = new JSONObject();
        body.put("name", "test");
        String goodsCategoryJson = body.toJSONString();

        String token = creatTestToken(1L, 0L, 100);

        byte[] responseString = manageClient.post().uri("/goods/shops/0/categories/11131/subcategories")
                .header("authorization", token)
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "        \"pid\":11131,\n" +
                "        \"name\": \"test\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse,  new String(responseString, StandardCharsets.UTF_8), false);


        //查询验证分类新增成功
        byte[] responseString2 = mallClient.get().uri("/goods/categories/11131/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse2="{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"pid\": 11131,\n" +
                "      \"name\": \"test\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, StandardCharsets.UTF_8), false);

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
    @Order(01)
    public void insertSubGoodsCategoryTest2()throws Exception {

        JSONObject body = new JSONObject();
        body.put("name", "test");
        String goodsCategoryJson = body.toJSONString();

        String token = creatTestToken(1L, 0L, 100);

        byte[] responseString = manageClient.post().uri("/goods/shops/0/categories/11119/subcategories")
                .header("authorization", token)
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();
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
    @Order(01)
    public void insertGoodsCategoryTest2() {

        JSONObject body = new JSONObject();
        body.put("name", "艺术品");
        String goodsCategoryJson = body.toJSONString();

        String token = creatTestToken(1L, 0L, 100);

        byte[] responseString = manageClient.post().uri("/goods/shops/0/categories/11122/subcategories")
                .header("authorization", token)
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.CATEGORY_NAME_SAME.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.CATEGORY_NAME_SAME.getMessage())
                .returnResult()
                .getResponseBodyContent();
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
    @Order(01)
    public void insertGoodsCategoryTest3() throws Exception{
        JSONObject body = new JSONObject();
        body.put("name", "");
        String goodsCategoryJson = body.toJSONString();

        String token = creatTestToken(1L, 0L, 100);

        byte[] responseString = manageClient.post().uri("/goods/shops/0/categories/11122/subcategories")
                .header("authorization", token)
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
//                .jsonPath("$.errmsg").isEqualTo("类目名称不能为空;")
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * description: 未登录新增类目
     * version: 1.0
     * date: 2020/12/2 20:48
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    @Order(01)
    public void insertGoodsCategoryTest4() throws Exception {

        JSONObject body = new JSONObject();
        body.put("name", "test");
        String goodsCategoryJson = body.toJSONString();

        byte[] responseString = manageClient.post().uri("/goods/shops/0/categories/11122/subcategories",0)
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 伪造token新增类目
     * version: 1.0
     * date: 2020/12/2 20:48
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    @Order(01)
    public void insertGoodsCategoryTest5() throws Exception {
        JSONObject body = new JSONObject();
        body.put("name", "test");
        String goodsCategoryJson = body.toJSONString();

        byte[] responseString = manageClient.post().uri("/goods/shops/0/categories/11122/subcategories")
                .header("authorization", "test")
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 修改类目信息(类目ID不存在)
     * version: 1.0
     * date: 2020/12/2 19:44
     * author: 张悦
     */
    @Test
    @Order(02)
    public void modifyCategory1() throws Exception {
        JSONObject body = new JSONObject();
        body.put("name", "test");
        String goodsCategoryJson = body.toJSONString();

        String token = creatTestToken(1L, 0L, 100);

        byte[] responseString = manageClient.put().uri("/goods/shops/0/categories/11199")
                .header("authorization", token)
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 修改类目信息 (类目名称重复)
     * version: 1.0
     * date: 2020/12/2 19:45
     * author: 张悦
     */
    @Test
    @Order(02)
    public void modifyCategory2() throws Exception {

        JSONObject body = new JSONObject();
        body.put("name", "收藏品");
        String goodsCategoryJson = body.toJSONString();

        String token = creatTestToken(1L, 0L, 100);

        byte[] responseString = manageClient.put().uri("/goods/shops/0/categories/11123")
                .header("authorization", token)
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.CATEGORY_NAME_SAME.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.CATEGORY_NAME_SAME.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 未登录修改类目
     * version: 1.0
     * date: 2020/12/2 20:48
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    @Order(02)
    public void modifyCategory3() throws Exception {

        JSONObject body = new JSONObject();
        body.put("name", "test");
        String goodsCategoryJson = body.toJSONString();

        byte[] responseString = manageClient.put().uri("/goods/shops/0/categories/11123")
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 伪造token修改类目
     * version: 1.0
     * date: 2020/12/2 20:48
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    @Order(02)
    public void modifyCategory4() throws Exception {

        JSONObject body = new JSONObject();
        body.put("name", "test");
        String goodsCategoryJson = body.toJSONString();

        byte[] responseString = manageClient.put().uri("/goods/shops/0/categories/11123")
                .header("authorization", "test")
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * description: 修改类目信息(成功)
     * version: 1.0
     * date: 2020/12/2 20:48
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    @Order(02)
    public void modifyCategory5() throws Exception {

        JSONObject body = new JSONObject();
        body.put("name", "test3");
        String goodsCategoryJson = body.toJSONString();

        String token = this.creatTestToken(1L, 0L, 100);

        byte[] responseString = manageClient.put().uri("/goods/shops/0/categories/11128")
                .header("authorization", token)
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        //查询验证是否修改成功
        byte[] responseString2 = mallClient.get().uri("/goods/categories/11127/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse2="{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": 11128,\n" +
                "      \"pid\": 11127,\n" +
                "      \"name\": \"test3\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 11129,\n" +
                "      \"pid\": 11127,\n" +
                "      \"name\": \"食器\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, StandardCharsets.UTF_8), false);

    }

    /**
     * description: 删除一级类目 (成功)
     * version: 1.0
     * date: 2020/12/2 19:45
     * author: 张悦
     */
    @Test
    @Order(03)
    public void deleteCategoryTest1() throws Exception {

        String token = this.creatTestToken(1L, 0L, 100);

        byte[] responseString = manageClient.delete().uri("/goods/shops/0/categories/11122")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        //查询验证一级类目删除
        byte[] responseString2 = mallClient.get().uri("/goods/categories/11122/subcategories")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();

        //查询验证二级类目下的商品变成没有分类的商品
        byte[] responseString5 = mallClient.get().uri("/goods/spus/11274")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse5 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"id\": 11274,\n" +
                "    \"category\": {\n" +
                "      \"id\": 0\n" +
                "    }\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse5, new String(responseString5, StandardCharsets.UTF_8), false);
    }


    /**
     * description: 删除二级类目 (成功)
     * version: 1.0
     * date: 2020/12/2 19:45
     * author: 张悦
     */
    @Test
    @Order(03)
    public void deleteCategoryTest5() throws Exception {

        String token = this.creatTestToken(1L, 0L, 100);

        byte[] responseString = manageClient.delete().uri("/goods/shops/0/categories/11128")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        //查询验证二级类目下的商品变成没有分类的商品
        byte[] responseString5 = mallClient.get().uri("/goods/spus/11273")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse5 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"id\": 11273,\n" +
                "    \"category\": {\n" +
                "      \"id\": 0\n" +
                "    }\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse5, new String(responseString5, StandardCharsets.UTF_8), false);
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
    @Order(03)
    public void deleteCategoryTest2() {

        String token = this.creatTestToken(1L, 0L, 100);

        byte[] responseString = manageClient.delete().uri("/goods/shops/0/categories/1")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo("操作的资源id不存在")
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 伪造token删除类目
     * version: 1.0
     * date: 2020/12/2 20:48
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    @Order(03)
    public void deleteCategoryTest3() {

        byte[] responseString = manageClient.delete().uri("/goods/shops/0/categories/11127")
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 未登录删除类目
     * version: 1.0
     * date: 2020/12/2 20:48
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    @Order(03)
    public void deleteCategoryTest4() {
        byte[] responseString = manageClient.delete().uri("/goods/shops/0/categories/11127")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 创建测试用token
     */
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        log.info(token);
        return token;
    }
//
////    private String login(String userName, String password) throws Exception {
////        LoginVo vo = new LoginVo();
////        vo.setUserName(userName);
////        vo.setPassword(password);
////        String requireJson = JacksonUtil.toJson(vo);
////        byte[] ret = manageClient.post().uri("/privileges/login").bodyValue(requireJson).exchange()
////                .expectStatus().isOk()
////                .expectBody()
////                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
////                .jsonPath("$.errmsg").isEqualTo("成功")
////                .returnResult()
////                .getResponseBodyContent();
////        return JacksonUtil.parseString(new String(ret, "UTF-8"), "data");
////        //endregion
////    }


}
