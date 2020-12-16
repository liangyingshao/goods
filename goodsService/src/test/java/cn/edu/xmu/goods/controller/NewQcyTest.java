package cn.edu.xmu.goods.controller;
import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.JacksonUtil;
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
//
//    @Value("${public-test.mallgate}")
//    private String mallGate;
    private String managementGate = "192.168.43.73:8881";
    private String mallGate = "192.168.43.73:8880";
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

    private String login(String userName, String password) throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();
        byte[] ret = manageClient.post().uri("/privileges/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return JacksonUtil.parseString(new String(ret, "UTF-8"), "data");
        //endregion
    }
    /**
     * 获取SKU所有状态 成功
     *
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
        String expectedResponse = "{ \"errno\": 0, \"data\": [ { \"name\": \"未上架\", \"code\": 0 }, { \"name\": \"上架\", \"code\": 1 } ,{ \"name\": \"已删除\", \"code\": 6 } ], \"errmsg\": \"成功\" }";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 店家新建商品SPU 成功
     */
    @Test
    public void addSpuTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        String skuJson = "{\"name\":\"iphone13\",\"decription\":\"最新系列\",\"specs\": \"{\"id\":1,\"name\":\"ipadspec\", \"specItems\":{\"id\":1, \"name\":\"color\"},{\"id\":2, \"name\":\"memory\"}}\" }";
        byte[] responseString = manageClient.post().uri("/shops/1/spus")
                .bodyValue(skuJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家新建商品SPU
     */
    @Test
    public void addSpuTest2() throws Exception {
        String token = this.login("13088admin", "123456");
        String skuJson = "{\"name\":\"iphone13\",\"decription\":\"最新系列\",\"specs\": \"{\"id\":1,\"name\":\"ipadspec\", \"specItems\":{\"id\":1, \"name\":\"color\"},{\"id\":2, \"name\":\"memory\"}}\" }";
        byte[] responseString = manageClient.post().uri("/shops/1/spus").header("authorization", token)
                .bodyValue(skuJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 查看一条商品SPU的详细信息 (成功)
     */
    @Test
    public void showSpuTest1() throws Exception {

        byte[] responseString = mallClient.get().uri("/goods/spus/290")
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(290)
                .jsonPath("$.data.name").isEqualTo("金和汇景•刘伟•粉彩瑞雪丰年瓷板")
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
    public void showSpuTest2() throws Exception {

        byte[] responseString = mallClient.get().uri("/goods/spus/1000")
                .exchange().expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * description: 店家SPU上架 成功
     */
    @Test
    public void onSaleSpuTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = mallClient.put().uri("/goods/shops/1/spus/333/onshelves")
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();


        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * description: 店家SPU上架 重复上架
     */
    @Test
    public void onSaleSpuTest2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString1 = mallClient.put().uri("/goods/shops/1/spus/333/onshelves")
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();

        byte[] responseString2 = mallClient.put().uri("/goods/shops/1/spus/333/onshelves")
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.STATE_NOCHANGE.getCode())
                .returnResult().getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString1, "UTF-8"), true);
    }

    /**
     * description: 店家SPU上架 spu不在shopid对应商铺
     */
    @Test
    public void onSaleSpuTest3() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString = mallClient.put().uri("/goods/shops/1/spus/333/onshelves")
                .exchange().expectStatus().isUnauthorized()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult().getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * description: 店家SPU上架 spu已为上架状态
     */
    @Test
    public void onSaleSpuTest4() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString = mallClient.put().uri("/goods/shops/1/spus/333/onshelves")
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.STATE_NOCHANGE.getCode())
                .returnResult().getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * description: 店家SPU下架 成功
     */
    @Test
    public void offSaleSpuTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = mallClient.put().uri("/goods/shops/1/spus/333/offshelves")
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();


        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * description: 店家SPU下架 重复下架
     */
    @Test
    public void offSaleSpuTest2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString1 = mallClient.put().uri("/goods/shops/1/spus/333/offshelves")
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();

        byte[] responseString2 = mallClient.put().uri("/goods/shops/1/spus/333/offshelves")
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.STATE_NOCHANGE.getCode())
                .returnResult().getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString1, "UTF-8"), true);
    }

    /**
     * description: 店家SPU下架 spu不在shopid对应商铺
     */
    @Test
    public void offSaleSpuTest3() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString = mallClient.put().uri("/goods/shops/1/spus/333/offshelves")
                .exchange().expectStatus().isUnauthorized()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult().getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * description: 店家SPU下架 spu已为下架状态
     */
    @Test
    public void offSaleSpuTest4() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString = mallClient.put().uri("/goods/shops/1/spus/333/offshelves")
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.STATE_NOCHANGE.getCode())
                .returnResult().getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * description: 店家修改SPU 成功
     */
    @Test
    public void modifySpuTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        String skuJson = "{\"name\":\"iphone13\",\"decription\":\"最新系列\",\"specs\": \"{\"id\":1,\"name\":\"ipadspec\", \"specItems\":{\"id\":1, \"name\":\"size\"},{\"id\":2, \"name\":\"color\"},{\"id\":3, \"name\":\"memory\"}}\" }";
        byte[] responseString = manageClient.put().uri("shops/1/spus/333").header("authorization", token)
                .bodyValue(skuJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * description: 店家修改SPU 修改的spu与shopID不对应
     */
    @Test
    public void modifySpuTest2() throws Exception {
        String token = this.login("13088admin", "123456");
        String skuJson = "{\"name\":\"iphone13\",\"decription\":\"最新系列\",\"specs\": \"{\"id\":1,\"name\":\"ipadspec\", \"specItems\":{\"id\":1, \"name\":\"size\"},{\"id\":2, \"name\":\"color\"},{\"id\":3, \"name\":\"memory\"}}\" }";
        byte[] responseString = manageClient.put().uri("shops/3/spus/333").header("authorization", token)
                .bodyValue(skuJson).exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage())
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
        String token = this.login("13088admin", "123456");
        String skuJson = "{\"name\":\"iphone13\",\"decription\":\"最新系列\",\"specs\": \"{\"id\":1,\"name\":\"ipadspec\", \"specItems\":{\"id\":1, \"name\":\"size\"},{\"id\":2, \"name\":\"color\"},{\"id\":3, \"name\":\"memory\"}}\" }";
        byte[] responseString = manageClient.put().uri("shops/1/spus/3333").header("authorization", token)
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
        String token = this.login("13088admin", "123456");
        String skuJson = "{\"name\":\"iphone13\",\"decription\":\"最新系列\",\"specs\": \"{\"id\":1,\"name\":\"ipadspec\", \"specItems\":{\"id\":1, \"name\":\"size\"},{\"id\":2, \"name\":\"color\"},{\"id\":3, \"name\":\"memory\"}}\" }";
        byte[] responseString = manageClient.put().uri("/goods/shops/1/spus/681/categories/124").header("authorization", token)
                .bodyValue(skuJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 将SPU加入二级分类 (试图加入一级分类)
     * date: 2020/12/02 20：40
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void addSpuCategoryTest2() throws Exception {
        String token = this.login("13088admin", "123456");
        String skuJson = "{\"name\":\"iphone13\",\"decription\":\"最新系列\",\"specs\": \"{\"id\":1,\"name\":\"ipadspec\", \"specItems\":{\"id\":1, \"name\":\"size\"},{\"id\":2, \"name\":\"color\"},{\"id\":3, \"name\":\"memory\"}}\" }";
        byte[] responseString = manageClient.put().uri("/goods/shops/1/spus/681/categories/125").header("authorization", token)
                .bodyValue(skuJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.CATEALTER_INVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.CATEALTER_INVALID.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 将SPU加入二级分类 (试图加入不存在分类)
     * date: 2020/12/02 20：40
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void addSpuCategoryTest3() throws Exception {
        String token = this.login("13088admin", "123456");
        String skuJson = "{\"name\":\"iphone13\",\"decription\":\"最新系列\",\"specs\": \"{\"id\":1,\"name\":\"ipadspec\", \"specItems\":{\"id\":1, \"name\":\"size\"},{\"id\":2, \"name\":\"color\"},{\"id\":3, \"name\":\"memory\"}}\" }";
        byte[] responseString = manageClient.put().uri("/goods/shops/1/spus/681/categories/666").header("authorization", token)
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
     * description: 移除SPU分类 (成功)
     * date: 2020/12/02 11：00
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void removeSpuCategoryTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/goods/shops/1/spus/681/categories/124").header("authorization", token)
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
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/goods/shops/1/spus/681/categories/888").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ACTIVITYALTER_INVALID.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * description: 移除SPU分类 移出分类与SPU原分类不一致
     */
    @Test
    public void removeSpuCategoryTest3() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/goods/shops/1/spus/681/categories/124").header("authorization", token)
                .exchange()
                .expectStatus().isNotModified()
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

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/goods/shops/1/spus/681/brands/110").header("authorization", token)
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
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/goods/shops/1/spus/681/brands/120").header("authorization", token)
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
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/goods/shops/1/spus/681/brands/110").header("authorization", token)
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
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/goods/shops/1/spus/681/brands/109").header("authorization", token)
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
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/goods/shops/1/spus/660").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 逻辑删除商品SPU 该SPU不存在
     */
    @Test
    public void deleteSpuBrandTest2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/goods/shops/1/spus/6666").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }
}


